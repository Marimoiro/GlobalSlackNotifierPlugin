
package org.jenkinsci.plugins.globalslack;

public final class SlackMessage{
    private String room;
    private String message;
    private boolean enable;
    private String color;

    public SlackMessage( String room,String message,boolean enable,String color )
    {
        this.room = room;
        this.message = message;
        this.enable = enable;
        this.color = color;
    }
    public String getRoom(){
        return room;
    }

    public String getMessage(){
        return message;
    }

    public boolean getEnable(){
        return enable;
    }

    /**
     * @return the color
     */
    public String getColor() {
        return color;
    }

    @Override
    public String toString(){
        return room + "\n" + message + "\n" + enable;
    }
}