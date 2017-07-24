
import java.io.File;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Dmitry
 */
public class FileManager {
    public static FileManagerResponse getItemTimeFile(Item item, int week) {
        
        FileManagerResponse response = new FileManagerResponse();
        
        File timeFile = new File(Config.getItemTimeString(item, week));
        if (!timeFile.exists()) {
            response.missingFile = true;
        } else {
            response.file = timeFile;
        }
        
        return response;
    }
        /*
        if (item.isLeaf()) {
            File timeFile = new File(item.getPath(getWeekPrepath(currentWeek)) + META_PATH + "time.txt");
            if (!timeFile.exists()) {
                item.setTime(null);
            } else {
                try (BufferedReader br = new BufferedReader(new FileReader(timeFile))) {
                    String line;
                    String startStr = "";
                    Period duration = null;
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm");
                    while ((line = br.readLine()) != null) {
                        if (line.startsWith("s:")) {
                            startStr = line.substring(2);
                        } else {
                            DateTime start = formatter.parseDateTime(startStr);
                            DateTime end = formatter.parseDateTime(line.substring(2));
                            if (duration == null) duration = new Period(start, end);
                            else duration = duration.plus(new Period(start, end));
                        }
                    }
                    item.setTime(duration);
                } catch (Exception e) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        } else {
            for (Item child: item.getChildren()) {
                parseTime(child);
            }
        }
        */
    
    static class FileManagerResponse {
        public boolean missingFile = false;
        File file = null;
    }
}
