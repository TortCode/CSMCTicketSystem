package edu.utdallas.csmc.web.helper;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class InstructorParseSectionHelper {

    public static List<String[]> parseRoster(File csvFile) throws IOException{

        try{
            String fileType = csvFile.getName().substring(csvFile.getName().lastIndexOf('.'));

            switch (fileType){
                case ".csv": {
                    return handleCSVFile(csvFile);
                }
                case ".xlsx": {
                    return handleExcelFile(csvFile);
                }
                default: {
                    throw new IOException();
                }
            }

        } catch(IOException e){
            System.out.println("Unable to read file...");
            return null;
        }
    }

    public static List<String[]> handleCSVFile(File file) throws FileNotFoundException {
        try{
            List<String[]> userResultList = new ArrayList<>();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            boolean first = true;
            while((line = br.readLine()) != null){
                if(first){
                    first = false;
                    continue;
                }
                String[] user = line.split(",");
                for(int i = 0; i < user.length; i++){
                    user[i] = user[i].substring(1, user[i].length() - 1);
                }
                userResultList.add(user);
            }
            return userResultList;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static List<String[]> handleExcelFile(File file){
        try{
            boolean first = true;
            List<String[]> userResultList = new ArrayList<>();
            String[] data = new String[3];
            FileInputStream fin = new FileInputStream(file);
            HSSFWorkbook wb = new HSSFWorkbook(fin);
            HSSFSheet sheet = wb.getSheetAt(0);
            Iterator<Row> itr = sheet.iterator();
            while(itr.hasNext()) {
                Row row = itr.next();
                if(first){
                    first = false;
                    continue;
                }

                Iterator<Cell> cellIterator = row.cellIterator();
                int i = 0;
                while(cellIterator.hasNext()){
                    Cell cell = cellIterator.next();
                    data[i] = cell.getStringCellValue();
                    i++;
                }
                userResultList.add(Arrays.copyOf(data, 3));
            }

            return userResultList;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
