package sondow.twitchpromo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This class serves the dual purposes of checking whether the specified Twitch user is currently
 * online, and saving that user's latest preview image file if they are online.
 *
 * @author @JoeSondow
 */
public class TwitchImageDownloader {

    public static DownloadResult checkAndDownload(String fileURL, String saveDir)
            throws IOException {

        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setInstanceFollowRedirects(false);
        int responseCode = httpConn.getResponseCode();

        boolean isOnline;
        File file;

        String jpgUrl404 = "https://static-cdn.jtvnw.net/ttv-static/404_preview-1280x720.jpg";
        if (HttpURLConnection.HTTP_MOVED_TEMP == responseCode) {
            // Redirect almost certainly means channel is offline.
            String location = httpConn.getHeaderField("Location");
            if (!jpgUrl404.equals(location)) {
                String msg = "Surprise redirect location: " + location + " instead of " + jpgUrl404;
                throw new RuntimeException(msg);
            }

            isOnline = false;
            file = null;

        } else if (HttpURLConnection.HTTP_OK == responseCode) {

            ReadableByteChannel rbc = Channels.newChannel(httpConn.getInputStream());
            String now = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT);
            String filename = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
            String fileName = now + "-" + filename;

            String saveFilePath = saveDir + File.separator + fileName;
            File downloadFolder = new File(saveDir);

            // downloadFolder.deleteOnExit();
            downloadFolder.mkdirs();

            FileOutputStream outputStream = new FileOutputStream(saveFilePath);
            outputStream.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

            outputStream.close();
            rbc.close();

            isOnline = true;
            file = new File(saveFilePath);

        } else {
            String msg = "Surprise response code " + responseCode + " for url " + fileURL;
            throw new RuntimeException(msg);
        }

        return new DownloadResult(file, isOnline);
    }

    public static String buildImageUrl(String user) {
        return "https://static-cdn.jtvnw.net/previews-ttv/live_user_" + user + "-1280x720.jpg";
    }

    public static void main(String[] args) throws Exception {
        checkAndDownload(buildImageUrl("littlesiha"), "src/main/resources");
    }

}
