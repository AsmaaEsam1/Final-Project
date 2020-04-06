package com.example.smart;

public class records {
    String recordName;
    String recordPath;

    public records(String mrecordName, String mrecordPath) {
        this.recordName = mrecordName;
        this.recordPath = mrecordPath;
    }

    public String getRecordName() {
        return recordName;
    }

    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    public String getRecordPath() {
        return recordPath;
    }

    public void setRecordPath(String recordPath) {
        this.recordPath = recordPath;
    }
}
