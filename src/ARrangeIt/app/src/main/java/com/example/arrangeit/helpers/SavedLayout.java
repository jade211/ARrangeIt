package com.example.arrangeit.helpers;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class SavedLayout {
    @Exclude private String id;
    private String userId;
    private String layoutName;
    private String screenshotUrl;
    private Timestamp timestamp;
    private List<Map<String, Object>> furniture;

    // Required empty constructor
    public SavedLayout() {}

    // Constructor for new layouts
    public SavedLayout(String userId, String layoutName, String screenshotUrl,
                       List<Map<String, Object>> furniture) {
        this.userId = userId;
        this.layoutName = layoutName;
        this.screenshotUrl = screenshotUrl;
        this.furniture = furniture;
        this.timestamp = Timestamp.now();
    }

    // Getters and setters
    @Exclude public String getId() { return id; }
    @Exclude public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getLayoutName() { return layoutName; }
    public void setLayoutName(String layoutName) { this.layoutName = layoutName; }
    public String getScreenshotUrl() { return screenshotUrl; }
    public void setScreenshotUrl(String screenshotUrl) { this.screenshotUrl = screenshotUrl; }
    public Date getTimestamp() { return timestamp.toDate(); }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
    public List<Map<String, Object>> getFurniture() { return furniture; }
    public void setFurniture(List<Map<String, Object>> furniture) { this.furniture = furniture; }
}