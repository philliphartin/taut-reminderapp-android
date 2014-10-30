package ulster.serg.tautreminderapp.controller.listviewadapter;

/**
 * Created by Phillip J Hartin on 21/10/13.
 */
public class ListItem {
    private long id;
    private String type;
    private String repeat;
    private String desc;
    private long unixTime;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public long getUnixTime() {
        return unixTime;
    }

    public void setUnixTime(long unixTime) {
        this.unixTime = unixTime;
    }

    @Override
    public String toString() {
        return desc;
    }
}
