import java.util.Date;

public class BookingForm {
    private int roomId;
    private Date date;
    private String time;

    // selectRoom, selectDate, selectTime - ревізія, без змін
    public void selectRoom(int roomId) {
        this.roomId = roomId;
    }

    public void selectDate(Date date) {
        this.date = date;
    }

    public void selectTime(String time) {
        this.time = time;
    }

    // getRoomId, getDate, getTime - ревізія, без змін
    public int getRoomId() {
        return roomId;
    }

    public Date getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}