package sondow.twitchpromo;

import java.io.File;

/**
 * Has a reference to the saved screenshot file if the Twitch user is online, or else indicates that
 * the user is offline.
 *
 * @author @JoeSondow
 */
public class DownloadResult {

    private final File file;
    private final boolean online;

    public DownloadResult(File file, boolean online) {
        super();
        this.file = file;
        this.online = online;
    }

    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     * @return the online
     */
    public boolean isOnline() {
        return online;
    }

}
