import java.io.*;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        String jcal, gcal, description, stime, etime;
        int currentyear;
        if(args.length>0) {
            jcal=args[0];
        } else {
            System.out.println("What is the path to the text file? Start from C: and use forward slashes (/).");
            System.out.println("For example: C:/Users/jcjensen/Desktop/calendar.txt");
            jcal = scan.nextLine();
        }
        if(args.length>1) {
            gcal=args[1];
        } else {
            System.out.println("Using the same type of file paths as above, which directory would you like the .csv to go in?");
            gcal = scan.nextLine();
        }
        if(gcal.charAt(gcal.length()-1) != '/') { gcal+="/";}
        gcal+="googlecalendar.csv";
        if(args.length>2) {
            currentyear = Integer.valueOf(args[2]);
        } else {
            System.out.println("What year would you like to generate the calendar for?");
            currentyear = Integer.valueOf(scan.next());
        }
        String line = null;
        int repeat,syear,eyear;
        Date firstoftheyear = new Date(1,1,currentyear);

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(jcal);
            FileWriter fileWriter = new FileWriter(gcal);
            // Always wrap FileReader in BufferedReader.
            BufferedWriter bWriter = new BufferedWriter(fileWriter);
            BufferedReader bReader = new BufferedReader(fileReader);

            bWriter.append("Start Date,End Date,All Day Event,Subject,Start Time,End Time");
            bWriter.newLine();

            while((line = bReader.readLine()) != null) {
                try {
                    if (!line.equals("")) {
                        line = line.replace("{","");
                        System.out.println(line);
                        repeat = 1;
                        try {
                            syear = Integer.parseInt(line.substring(13, 17));
                        } catch (Exception e) {
                            syear = currentyear;
                        }
                        try {
                            eyear = Integer.parseInt(line.substring(24, 28));
                        } catch (Exception e) {
                            eyear = currentyear;
                        }
                        Date sdate = new Date(Integer.parseInt(line.substring(7, 9)), Integer.parseInt(line.substring(10, 12)), syear);
                        Date edate = new Date(12, 31, currentyear);
                        if (sdate.getYear() <= currentyear) {
                            //System.out.println("Checkpoint #1");
                            if (!line.substring(0, 1).equals("A")) {
                                description = line.substring(29);
                                stime=StartTime(description);
                                etime=EndTime(description);
                                edate = new Date(Integer.parseInt(line.substring(18, 20)), Integer.parseInt(line.substring(21, 23)), eyear);
                                if (!line.substring(0, 2).contains(" ")) {//If it is the day of a week
                                    if (!line.substring(5, 6).equals(" ")) { //If has specified repeat
                                        repeat = Integer.parseInt(line.substring(5, 6));
                                    }
                                    if (line.substring(2, 3).equals("L")) { //If last of the type of day
                                        //System.out.println("Checkpoint #2");
                                        Date lastone = LastOfMonth(line.substring(0, 2), sdate.getMonth(), currentyear);
                                        //System.out.println("Checkpoint #3");
                                        if (lastone.getDayDifference(sdate) < 0) {
                                            lastone = LastOfMonth(line.substring(0, 2), sdate.getMonth() + 1, currentyear);
                                        }
                                        //System.out.println("Checkpoint #4");
                                        while (edate.getDayDifference(lastone) >= 0) {
                                            //System.out.println("Checkpoint #5");
                                            if (lastone.getDayDifference(firstoftheyear) >= 0) {
                                                bWriter.append(lastone.toString() + "," + lastone.toString() + ",True," + description + ",,");
                                                bWriter.newLine();
                                            }
                                            lastone = LastOfMonth(line.substring(0, 2), lastone.getMonth() + repeat, currentyear);
                                        }
                                    } else if (line.substring(2, 3).equals(" ")) {//If every of that day in the date range
                                        Date realStartDate = sdate;
                                        while (!realStartDate.getDayOfWeek().equals(line.substring(0, 2))) {
                                            realStartDate.addDays(1);
                                        }
                                        while (edate.getDayDifference(realStartDate) >= 0) {
                                            if (realStartDate.getDayDifference(firstoftheyear) >= 0) {
                                                bWriter.append(realStartDate.toString() + "," + realStartDate.toString() + ",True," + description + ",,");
                                                bWriter.newLine();
                                            }
                                            realStartDate.addDays(7 * repeat);
                                        }
                                    } else {//If there is a number after the day
                                        Date firstone = FirstOfMonth(line.substring(0, 2), sdate.getMonth(), currentyear);
                                        firstone.addDays(7 * (Integer.valueOf(line.substring(2, 3)) - 1));
                                        if (firstone.getDayDifference(sdate) < 0) {
                                            firstone = FirstOfMonth(line.substring(0, 2), sdate.getMonth() + 1, currentyear);
                                            firstone.addDays(7 * (Integer.valueOf(line.substring(2, 3)) - 1));
                                        }
                                        while (edate.getDayDifference(firstone) >= 0) {
                                            if (firstone.getDayDifference(firstoftheyear) >= 0) {
                                                bWriter.append(firstone.toString() + "," + firstone.toString() + ",True," + description + ",,");
                                                bWriter.newLine();
                                            }
                                            firstone = FirstOfMonth(line.substring(0, 2), firstone.getMonth() + repeat, currentyear);
                                            firstone.addDays(7 * (Integer.valueOf(line.substring(2, 3)) - 1));
                                        }
                                    }
                                } else if (line.substring(0, 1).equals("D")) { //If daily event
                                    if (!line.substring(5, 6).equals(" ")) { //If has specified repeat
                                        repeat = Integer.parseInt(line.substring(5, 6));
                                    }
                                    Date realStartDate;
                                    if (currentyear > sdate.getYear()) {
                                        realStartDate = new Date(1, 1, currentyear);
                                        while (realStartDate.getDayDifference(sdate) % repeat != 0) {
                                            realStartDate.addDays(realStartDate.getDayDifference(sdate) % repeat);
                                        }
                                    } else {
                                        realStartDate = sdate;
                                    }
                                    while (realStartDate.getYear() == currentyear && edate.getDayDifference(realStartDate) >= 0) {
                                        if (realStartDate.getDayDifference(firstoftheyear) >= 0) {
                                            bWriter.append(realStartDate.toString() + "," + realStartDate.toString() + ",True," + description + ",,");
                                            bWriter.newLine();
                                        }
                                        realStartDate.addDays(repeat);
                                    }
                                } else if (line.substring(0, 1).equals("M")) {
                                    if (!line.substring(5, 6).equals(" ")) { //If has specified repeat
                                        repeat = Integer.parseInt(line.substring(5, 6));
                                    }
                                    Date realStartDate;
                                    if (currentyear > sdate.getYear()) {
                                        realStartDate = new Date(1, sdate.getDay(), currentyear);
                                        while (realStartDate.getMonthDifference(sdate) % repeat != 0) {
                                            //System.out.println("First while loop");
                                            realStartDate.addMonths(realStartDate.getMonthDifference(sdate) % repeat);
                                        }
                                    } else {
                                        realStartDate = sdate;
                                    }
                                    while (realStartDate.getYear() == currentyear && edate.getMonthDifference(realStartDate) >= 0) {
                                        //System.out.println("Second while loop: " + realStartDate.getYear() + ", " + edate.getMonthDifference(realStartDate) + ", Dates comparing: " + edate.toString() + " " + realStartDate.toString());
                                        if (realStartDate.getDayDifference(firstoftheyear) >= 0) {
                                            bWriter.append(realStartDate.toString() + "," + realStartDate.toString() + ",True," + description + ",,");
                                            bWriter.newLine();
                                        }
                                        realStartDate.addMonths(repeat);
                                    }
                                } else if (line.substring(0, 1).equals("Y")) {
                                    if (!line.substring(5, 6).equals(" ")) { //If has specified repeat
                                        repeat = Integer.parseInt(line.substring(5, 6));
                                    }
                                    Date thisyearsdate = new Date(sdate.getMonth(), sdate.getDay(), currentyear);
                                    if ((currentyear - sdate.getYear()) % repeat == 0 && edate.getDayDifference(thisyearsdate) >= 0) { //If the event falls in the current year, insert it into the calendar
                                        String eventtext = description;
                                        //System.out.println(eventtext.contains("#####"));
                                        //System.out.println(event);
                                        if (eventtext.contains("#####")) {
                                            eventtext = eventtext.replace("#####", (currentyear - sdate.getYear()) + " Birthday");
                                        }
                                        if (thisyearsdate.getDayDifference(firstoftheyear) >= 0) {
                                            bWriter.append(thisyearsdate.toString() + "," + thisyearsdate.toString() + ",True," + eventtext + ",,");
                                            bWriter.newLine();
                                        }
                                    }
                                }
                            } else { //If an absolute event
                                if (sdate.getDayDifference(firstoftheyear) >= 0) {
                                    description=line.substring(18);
                                    bWriter.append(sdate.toString() + "," + sdate.toString() + ",True," + description + ",,");
                                    bWriter.newLine();
                                }
                            }
                        }
                    }
                }
                catch (Exception e) {
                    System.out.println("'" + line + "' couldn't be read. Skipping to next line...");
                }
            }

            // Always close files.
            bReader.close();
            bWriter.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to find file '" + jcal + "'. Please try again. Restarting...");
            main(new String[]{});
        }
        catch (IOException io) {
            System.out.println("Weird io exception.");
        }
    }
    public static Date LastOfMonth(String dayofweek,int month,int year) {
        Date testdate = new Date(month,0,year);
        while(!testdate.getDayOfWeek().equals(dayofweek)) {
            //System.out.println("Inside this while loop...");
            testdate.addDays(-1);
            //System.out.println(testdate.getDayOfWeek() + " " + testdate.toString());
        }
        return testdate;
    }
    public static Date FirstOfMonth(String dayofweek,int month,int year) {
        Date testdate = new Date(month,1,year);
        while(!testdate.getDayOfWeek().equals(dayofweek)) {
            testdate.addDays(1);
        }
        return testdate;
    }
    public static String StartTime(String description) {
        int amindex=description.toLowerCase().indexOf("a ");
        int pmindex=description.toLowerCase().indexOf("p ");
        int startindex,endindex,colonindex=0;
        String endtime;
        if(amindex==-1 && pmindex==-1) { return "";}
        else if (0-amindex<0-pmindex){
            startindex=amindex-1;
            endindex=amindex;
            endtime=" AM";
        } else {
            startindex=pmindex-1;
            endindex=pmindex;
            endtime=" PM";
        }
        while(startindex>=0) {
            if(!description.substring(startindex,startindex+1).equals(":")) {
                try{
                    Integer.valueOf(description.substring(startindex,startindex+1));
                } catch (Exception e) {
                    break;
                }
            } else {
                colonindex=startindex;
            }
            startindex--;
        }
        startindex++; //Get back to the good times. :D
        if(colonindex==0) {
            return description.substring(startindex,endindex) + ":00" + endtime;
        } else {
            return description.substring(startindex, endindex) + endtime;
        }
    }
    public static String EndTime(String description) {
        int amindex=description.lastIndexOf("a ");
        int pmindex=description.lastIndexOf("p ");
        int startindex,endindex,colonindex=0;
        String endtime;
        if(amindex==-1 && pmindex==-1) { return "";}
        else if (amindex>pmindex){
            startindex=amindex-1;
            endindex=amindex;
            endtime=" AM";
        } else {
            startindex=pmindex-1;
            endindex=pmindex;
            endtime=" PM";
        }
        while(startindex>=0) {
            if(!description.substring(startindex,startindex+1).equals(":")) {
                try{
                    Integer.valueOf(description.substring(startindex,startindex+1));
                } catch (Exception e) {
                    break;
                }
            } else {
                colonindex=startindex;
            }
            startindex--;
        }
        startindex++; //Get back to the good times. :D
        if(colonindex==0) {
            return description.substring(startindex,endindex) + ":00" + endtime;
        } else {
            return description.substring(startindex, endindex) + endtime;
        }
    }
    public static String AddHour(String time) {

    }
}