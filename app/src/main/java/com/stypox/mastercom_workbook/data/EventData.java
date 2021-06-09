package data;

/**
 * The object that represents a single event
 */
public class Event {
    private String title;
    private String teacher;
    private String description;

    private String day;
    private String month;
    private String dayName;

    private String timeEnd;
    private String timeStart;

    private boolean isTomorrow;

    public Event (
        String title, String description, String teacher,
        String day, String month, String dayName, boolean isTomorrow,
        String timeStart, String timeEnd) {

        this.title = title;
        this.description = description;
        this.teacher = teacher;
        this.day = day;
        this.month = month;
        this.dayName = dayName;
        this.isTomorrow = isTomorrow;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public String toString () {
        return 
            this.getDay() + " " +
            this.getMonth() + " " +
            this.getDayName() + " from " +
            this.getTimeStart() + " to " + 
            this.getTimeEnd() + " " +
            "isTomorrow: " +
            this.isTomorrow() + "\n" +
            this.getTeacher() + "\n" +
            this.getTitle() + "\n" +
            this.getDescription();
    }
    
    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public boolean isTomorrow() {
        return isTomorrow;
    }

    public void setTomorrow(boolean isTomorrow) {
        this.isTomorrow = isTomorrow;
    }
}
