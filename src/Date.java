public class Date {
    int month,day,year; //Everything starts at 1
    int[] days = new int[]{31,28,31,30,31,30,31,31,30,31,30,31};
    int[] monthkeyvalues = new int[]{1,4,4,0,2,5,0,3,6,1,4,6};
    int[] centurycodes = new int[]{4,2,0,6};
    String[] weekdayindex = new String[]{"Sa","Su","Mo","Tu","We","Th","Fr"};
    public Date(int month, int day, int year) {
        this.month=month;
        this.year=year;
        if(day==0) {this.day=days[month-1];}
        else {this.day=day;}
        equalizeValues();
    }
    public int getMonth() {
        return month;
    }
    public int getDay() {
        return day;
    }
    public int getYear() {
        return year;
    }
    public void addMonths(int monthstoadd) {
        month+=monthstoadd;
        equalizeValues();
    }
    public void addDays(int daystoadd) {
        day+=daystoadd;
        equalizeValues();
    }
    public int getDaysPastInYear() {//Includes present day
        int dayspassed=0;
        for(int i=1;i<month;i++) {//Add days in previous months
            dayspassed+=days[i-1];
        }
        if(month>=3 && year%4==0) { dayspassed++;} //Leap Day
        dayspassed+=day;
        return dayspassed;
    }
    public int getMonthDifference(Date otherdate) {
        if(getYear()==otherdate.getYear()) {
            return getMonth() - otherdate.getMonth();
        } else if (getYear() > otherdate.getYear()){
            int diff=0;
            for(int i=otherdate.getYear() + 1;i<getYear();i++) {
                diff+=12;
            }
            diff+=getMonth() + (12-otherdate.getMonth());
            return diff;
        } else {
            int diff=0;
            for(int i=otherdate.getYear() - 1;i>getYear();i--) {
                diff-=12;
            }
            diff-=(otherdate.getMonth() + (12-getMonth()));
            return diff;
        }
    }
    public int getDayDifference(Date otherdate) {//Assume
        if(getYear()==otherdate.getYear()) {
            return getDaysPastInYear() - otherdate.getDaysPastInYear();
        } else if (getYear() > otherdate.getYear()){
            int diff=0;
            for(int i=otherdate.getYear() + 1;i<getYear();i++) {
                if(i%4!=0) {
                    diff+=365;
                } else {
                    diff+=366;
                }
            }
            diff+=getDaysPastInYear() + (365-otherdate.getDaysPastInYear());
            if (otherdate.getYear()%4==0) { diff++;}
            return diff;
        } else {
            int diff=0;
            for(int i=otherdate.getYear() - 1;i>getYear();i--) {
                if(i%4!=0) {
                    diff-=365;
                } else {
                    diff-=366;
                }
            }
            diff-= (otherdate.getDaysPastInYear() + (365-getDaysPastInYear()));
            if (getYear()%4==0) { diff--;}
            return diff;
        }
    }
    public String getDayOfWeek() { //Uses the Key Value Method for the Gregorian calendar.
        int answer = getYear() % 100;
        answer = answer/4;
        answer += getDay();
        answer += monthkeyvalues[getMonth()-1];
        if(getYear()%4==0 && getMonth()<=2) { answer--;}
        answer += centurycodes[((getYear()-100)%400)/100];
        answer += getYear() % 100;
        answer = answer%7;
        return weekdayindex[answer];
    }
    public String toString() {
        String strday, strmonth;
        if (getDay() < 10) {
            strday = "0" + getDay();
        } else {
            strday = String.valueOf(getDay());
        }
        if (getMonth() < 10) {
            strmonth = "0" + getMonth();
        } else {
            strmonth = String.valueOf(getMonth());
        }
        return strmonth + "/" + strday + "/" + getYear();
    }
    private void equalizeValues() {
        //System.out.println("Before equalized: " + toString());
        int totaldays = day;
        for(int i=1;i<month;i++) {
            totaldays += days[(i-1)%12];
        }
        while(totaldays>365) {
            if(year%4==0 && totaldays>366) {
                year++;
                totaldays-=366;
            } else if (year%4!=0) {
                totaldays-=365;
                year++;
            } else { break;}
        }
        month=1;
        while(month<12 && totaldays-(days[month-1]) > 0) {
            totaldays-=days[month-1];
            month++;
        }
        day=totaldays;
        //System.out.println("After equalized: " + toString());
    }
}