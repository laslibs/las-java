# las-java is a Java library for parsing .Las file (Geophysical well log files).

### Currently supports only version 2.0 of [LAS Specification](https://www.cwls.org/wp-content/uploads/2017/02/Las2_Update_Feb2017.pdf).  For more information about this format, see the Canadian Well Logging Society [product page](https://www.cwls.org/products/).

## How to use

### Installing

Download .jar and add jar file as a Module to your Java project:

Download from https://github.com/engage-so/engage-java/releases/tag/v1.0.0

On Intellij IDEA: File -> Project Structure -> Modules -> Dependencies Tab -> Add -> JARs or Directories -> Attach jar

On Netbeans: Project properties -> Libraries -> Compile -> ADD JAR/folder -> Add Jar

### Usage

create a LasJava object
  ```java
las = new LasJava("./src/test/java/com/laslibs/sample/example1.las", true);
  ```

  You can also pass LAS file contents directly to constructor

  ```java
  String lasString = 
        "~VERSION INFORMATION
        "VERS.                          2.0 :   CWLS LOG ASCII STANDARD -VERSION 2.0
        "WRAP.                          NO  :   ONE LINE PER DEPTH STEP
        "~WELL INFORMATION
        "#MNEM.UNIT              DATA                       DESCRIPTION
        "#----- -----            ----------               -------------------------
        "STRT    .M              1670.0000                :START DEPTH.........
  // add false to constructor options
        
  LasJava myLas = new LasJava(lasString, false);
  ```

### Read data

  > Use las.getData() to get a 2-dimensional array containing the readings of each log,
  > Or las.getDataStripped() to get the same as above but with all rows containing null values stripped off

  ```java
  LasJava myLas = new LasJava(lasString, false);

  String[][] data = las.getData();
  System.out.println(Arrays.deepToString(value));
  
  /**
     [[2650.0, 177.825, -999.25, -999.25],
      [2650.5, 182.5, -999.25,-999.25],
      [2651.0,180.162, -999.25, -999.25],
      [2651.5, 177.825, -999.25, -999.25],
      [2652.0, 177.825, -999.25, -999.25] ...]
    */

  String[][] dataStripped = las.getDataStripped();
  System.out.println(Arrays.deepToString(value));
  
  /**
   [[2657.5, 212.002, 0.16665, 1951.74597],
   [2658.0, 201.44, 0.1966, 1788.50696],
   [2658.5, 204.314, 0.21004, 1723.21204],
   [2659.0, 212.075, 0.22888, 1638.328],
   [2659.5, 243.536, 0.22439, 1657.91699]...]
   */
  ```

- Get the log headers

    ```java
        // ...
        String[] headers = las.getHeader();
        System.out.println(headers);
        // ['DEPTH', 'GR', 'NPHI', 'RHOB']
       // ...
    ```

- Get the log headers descriptions

    ```java
        //...
        String[] headers = las.getHeaderAndDescr();
        System.out.println(headers);
        // {DEPTH: 'DEPTH', GR: 'Gamma Ray', NPHI: 'Neutron Porosity', RHOB: 'Bulk density'}
        // ...
    ```

- Get a particular column, say Gamma Ray log

    ```Java
        // ...
        String[] column = las.getColumn('GR');
        System.out.println(column);
        // [-999.25, -999.25, -999.25, -999.25, -999.25, 122.03, 123.14, ...]
        // ...
    ```
    ```Java
        // ...
        // get column with null values stripped
        String[] column = las.getColumnStripped('GR');
        System.out.println(column);
        // [61.61, 59.99, 54.02, 50.87, 54.68, 64.39, 77.96, ...]
        // ...
    ```
    > Note this returns the column, after all the data has been stripped off their null values, which means that valid data in a particular column would be stripped off if there is another column that has a null value

- Get the Well Parameters

  ### Presents a way of accessing the details of individual well parameters.

  ### The details include the following:

        1. description - Description/ Full name of the well parameter
        2. units - Its unit measurements
        3. value - Value

  ```java
    // ...
    Map<String, Map<String, String>> well = las.getWellParams()
    String start = well.get("STRT").get("value") // 1670.0
    String stop = well.get("STOP").get("value") // 1669.75
    String null_value = well.get("NULL").get("value") //  -999.25
    // Any other well parameter present in the file, can be gotten with the same syntax above
    // ...
  ```

- Get the Curve Parameters

  ### Presents a way of accessing the details of individual log columns.

  ### The details include the following:

        1. description - Description/ Full name of the log column
        2. units - Unit of the log column measurements
        3. value - API value of the log column

  ```Java
    // ...
    Map<String, Map<String, String>> well = las.getCurveParams()
    String NPHI = curve.get("NPHI").get("description") // 'Neutron Porosity'
    String RHOB = curve.get("RHOB").get("description") // 'Bulk density'
    // This is the same for all log column present in the file
    // ...
  ```

- Get the Parameters of the well

  ### The details include the following:

        1. description - Description/ Full name of the log column
        2. units - Unit of the log column measurements
        3. value - API value of the log column

  ```Java
    // ...
    Map<String, Map<String, String>> well = las.getWellParams(); // 'BOTTOM HOLE TEMPERATURE'
    String BHT = param.get("BHT").get("description") // 'BOTTOM HOLE TEMPERATURE'
    String BHTValaue = param.get("BHT").get("value") // 35.5
    String BHTUnits = param.get("BHT").get("units") // 'DEGC'
    // This is the same for all well parameters present in the file
    // ...
  ```

- Get the number of rows and columns

    ```Java
        // ...
        int numRows =  las.rowCount() // 4
        int numColumns =  las.columnCount() // 30
        // ...
    ```

- Get the version and wrap

    ```java
        // ...
        double version = las.getVersion() // '2.0'
        boolean wrap = las.getWrap() // true
        // ...
    ```

- Get other information
  ```Java
      // ...
      String other = las.other()
      System.out.println(other)
      // Note: The logging tools became stuck at 625 metres causing the data between 625 metres and 615 metres to be invalid.
      // ...
  ```

- Export to CSV

  ```Java
      //...
      las.toCsv("mainTest")
      // Written successfully
      //...
  ```

  > result.csv

  | DEPT | RHOB    | GR      | NPHI  |
    | ---- | ------- | ------- | ----- |
  | 0.5  | -999.25 | -999.25 | -0.08 |
  | 1.0  | -999.25 | -999.25 | -0.08 |
  | 1.5  | -999.25 | -999.25 | -0.04 |
  | ...  | ...     | ...     | ...   |
  | 1.3  | -999.25 | -999.25 | -0.08 |

  Or get the version of csv with null values stripped

  ```Java
      // ...
      las.toCsvStripped('clean')
      // Written successfully
      // ...
  ```

  > clean.csv

  | DEPT | RHOB  | GR   | NPHI  |
    | ---- | ----- | ---- | ----- |
  | 80.5 | 2.771 | 18.6 | -6.08 |
  | 81.0 | 2.761 | 17.4 | -6.0  |
  | 81.5 | 2.752 | 16.4 | -5.96 |
  | ...  | ...   | ...  | ...   |
  | 80.5 | 2.762 | 16.2 | -5.06 |

- ## Support
  las-java is an MIT-licensed open source project. You can help it grow by becoming a sponsor/supporter.
