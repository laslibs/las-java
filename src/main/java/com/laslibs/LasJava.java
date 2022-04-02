package com.laslibs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class LasJava
{
    private String lasString;

    public LasJava(String fileSource, Boolean loadFile){
        if(loadFile){
            File file = new File(fileSource);
            try {
                this.lasString = new String(Files.readAllBytes(file.toPath()));
            } catch (IOException exception){
                throw new IllegalArgumentException("Pass a valid file" + exception.getMessage());
            }
            return;
        }
        this.lasString = fileSource;
    }

    private <T> ArrayList<T[]> chunk(T[] data, int size){
        ArrayList<T[]> newData = new ArrayList<>();
        int index = 0;
        while (index < data.length){
            newData.add(getSliceOfArray(data, index, index+size));
            index+=size;
        }
        return newData;
    }

    private <T> T[] getSliceOfArray(T[] arr, int startIndex, int endIndex) {
        return Arrays
                .copyOfRange (
                        arr,
                        startIndex,
                        endIndex);
    }
    private String removeComment(String input){
        return Arrays.stream(
                input
                .trim()
                .split("\n"))
                .map(val -> val.replaceAll("^\\s+", ""))
                .filter(f -> !(f.charAt(0) == '#'))
                .collect(Collectors.joining("\n"));
    }

    private String[] metaData(){
        String metaPart = this.lasString.split("~V(?:\\w*\\s*)*\\n\\s*")[1]
                .split("~")[0];
        metaPart = removeComment(metaPart);
        List<String[]> refinedMeta = Arrays.stream(metaPart.split("\n"))
                .map(m -> getSliceOfArray(m.split("\s{2,}|\s*:"),0,2 ))
                .collect(Collectors.toList());
        return refinedMeta.stream().map(r -> r[1]).toArray(String[]::new);
    }

    public Boolean getWrap(){
        String[] meta = metaData();
        return meta[1].equalsIgnoreCase("yes");
    }

    public Double getVersion(){
        String[] meta = metaData();
        return Double.parseDouble(meta[0]);
    }

    private Map<String, Map<String, String>> getProperty(String type) {
        // Set regex for the different properties
        HashMap<String, String> props = new HashMap<>() {{
            put("curve", "~C(?:\\w*\\s*)*\\n\\s*");
            put("param", "~P(?:\\w*\\s*)*\\n\\s*");
            put("well", "~W(?:\\w*\\s*)*\\n\\s*");
        }};
        String[] ls = lasString.split(props.get(type));
        String wp = "";
        if (ls.length > 0) {
            wp = ls[1].split("~")[0];
            wp = removeComment(wp);
        }
        Map<String, Map<String, String>> wellProps = new HashMap<>();
        if (!(wp.length() > 0)) {
            throw new LasException("The property " + type + " does not exist");
        }
        Arrays.stream(wp.split("\n")).forEach((str) -> {
            String obj = str.replaceAll("\\s *[.]\\s +", "   none   ");
            String title = obj.split("[.]|\\s +")[0];
            String unit = obj.trim().split("^\\w+\\s*[.]*s*")[1]
                    .split("\\s +")[0];
            String description = obj.split("[:]")[1].trim().isEmpty() ? "none" : obj.split("[:]")[1].trim();
            String[] valPrep = obj.split("[:]")[0].split("\\s{2,}\\w*\\s{2,}");
            String value = valPrep.length > 2 && (valPrep[valPrep.length - 1].isEmpty() || valPrep[valPrep.length - 1] == null) ? valPrep[valPrep.length - 2] : valPrep[valPrep.length - 1];
            String finalValue = value.length() > 0 ? value.trim() : value;
            wellProps.put(title, new HashMap<>() {{
                put("unit", unit);
                put("value", finalValue);
                put("description", description);
            }});
        });
        return wellProps;
    }

    public Map<String, Map<String, String>> getCurveParams(){
        return getProperty("curve");
    }

    public Map<String, Map<String, String>> getWellParams(){
        return getProperty("well");
    }

    public Map<String, Map<String, String>> getLogParams(){
        return getProperty("param");
    }

    public String other(){
        String otherVal = lasString.split("~O(?:\\w*\s*)*\n\s*")[1];
        String str = "";
        if (!otherVal.isEmpty()) {
          String other = otherVal
                    .split("~")[0]
                    .replaceAll("\n\s*/g", " ")
            .trim();
            str = removeComment(other);
        }
        if (str.length() <= 0) {
            return "";
        }
        return str;
    }

    public String[] getHeader(){
        String sth = lasString.split("~C(?:\\w*\s*)*\n\s*")[1].split("~")[0];
        String uncommentedSth = removeComment(sth).trim();
        if (uncommentedSth.isEmpty()) {
            throw new LasException("There is no header in the file");
        }
        return Arrays.stream(uncommentedSth.split("\n"))
                .map(m -> m.trim().split("\s+|[.]")[0])
                .toArray(String[]::new);
    }

    public Map<String, String> getHeaderAndDescr(){
        Map<String, Map<String, String>> cur = this.getProperty("curve");
        Map<String, String> response = new HashMap<>();
        for (Map.Entry<String, Map<String, String>> entry : cur.entrySet()) {
            String desc = entry.getValue().get("description") == "none" ? entry.getKey() : entry.getValue().get("description");
            response.put(entry.getKey(), desc);
        }
        if (response.isEmpty()) {
            throw new LasException("Poorly formatted ~curve section in the file");
        }
        return response;
    }

    private Double convertToValue(String string) {
        return Double.parseDouble(string);
    }

    public String[][] data(){
        String[] hds = getHeader();
        int totalHeadersLength = hds.length;
        String[] data = Arrays.stream(this.lasString.split("~A(?:[\\x20-\\x7E])*(?:\r\n|\r|\n)")[1].trim()
                        .split("\s+|\n")).map(String::trim)
                        .toArray(String[]::new);
        return chunk(data, totalHeadersLength).toArray(String[][]::new);
    }

    public String[][] dataStripped(){
        String[] hds = getHeader();
        Map<String, Map<String, String>> well = this.getProperty("well");
        String nullValue = well.get("NULL").get("value");
        int totalHeadersLength = hds.length;
        String[] data = Arrays.stream(this.lasString.split("~A(?:[\\x20-\\x7E])*(?:\r\n|\r|\n)")[1].trim()
                        .split("\s+|\n")).map(String::trim)
                        .toArray(String[]::new);
        ArrayList<String[]> con = chunk(data, totalHeadersLength);
        ArrayList<String[]> filtered = new ArrayList<>();
        for(String[] str : con){
            boolean check = Arrays.asList(str).contains(nullValue);
            if (check) continue;
            filtered.add(str);
        }
        return filtered.toArray(String[][]::new);
    }

    public int rowCount(){
        return this.data().length;
    }

    public int columnCount(){
        return this.getHeader().length;
    }

    public String[] getColumn(String column){
        String[] hds = getHeader();
        String[][] sB = data();
        int index = Arrays.asList(hds).indexOf(column.toLowerCase());
        if(index < 0) index = Arrays.asList(hds).indexOf(column.toUpperCase());
        if (index < 0) {
            throw new LasException(column+" Does not exist");
        }
        int finalIndex = index;
        return Arrays.stream(sB).map(c -> c[finalIndex]).toArray(String[]::new);
    }

    public String[] getColumnStripped(String column){
        String[] hds = getHeader();
        String[][] sB = dataStripped();
        int index = Arrays.asList(hds).indexOf(column.toUpperCase());
        if(index < 0) index = Arrays.asList(hds).indexOf(column.toLowerCase());
        if (index < 0) {
            throw new LasException(column+" Does not exist");
        }
        int finalIndex = index;
        return Arrays.stream(sB).map(c -> c[finalIndex]).toArray(String[]::new);
    }

    public boolean toCsv(String fileName){
        try {
            String[] headers = getHeader();
            String[][] data = data();
            prepareCsvAndWrite(fileName, headers, data);
            System.out.println("Writtern successfullyß");
            return true;
        } catch (IOException e) {
            throw new LasException("An error occurred", e);
        }
    }
    public boolean toCsvStripped(String fileName){
        try {
            String[] headers = getHeader();
            String[][] data = dataStripped();
            prepareCsvAndWrite(fileName, headers, data);
            System.out.println("Written successfully");
            return true;
        } catch (IOException e) {
            throw new LasException("An error occurred", e);
        }
    }

    private void prepareCsvAndWrite(String fileName, String[] headers, String[][] data) throws IOException {
        String rHd = String.join(",", headers) + '\n';
        StringBuilder rDataBuilder = new StringBuilder();
        for(String[] d: data){
            rDataBuilder.append(String.join(",", d)).append("\n");
        }
        String rData = rDataBuilder.toString();
        File file = new File(fileName+".csv");
        FileWriter fileWriter = new FileWriter(file.getPath());
        fileWriter.write(rHd + rData);
        fileWriter.close();
    }
}
