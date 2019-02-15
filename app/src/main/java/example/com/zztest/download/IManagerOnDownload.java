package example.com.zztest.download;

import java.util.List;

public interface IManagerOnDownload {
    void onFinished(Task task);

    void onSizeChanged(List<Task> tasks, int currentSize);
}
