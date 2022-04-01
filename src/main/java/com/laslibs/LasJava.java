package com.laslibs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    public String[] metaData(){
        String metaPart = this.lasString.split("~V(?:\\w*\\s*)*\\n\\s*")[1]
                        .split("~")[0];
        metaPart = removeComment(metaPart);
        List<String[]> refinedMeta = Arrays.stream(metaPart.split("\n"))
                .map(m -> getSliceOfArray(m.split("\s{2,}|\s*:"),0,2 ))
                .collect(Collectors.toList());
        return refinedMeta.stream().map(r -> r[1]).toArray(String[]::new);
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
}
