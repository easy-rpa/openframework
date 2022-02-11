package eu.ibagroup.easyrpa.openframework.google.drive.model;

public class GFileId {

    private String id;

    public GFileId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }

    public static GFileId of(String id){
        return new GFileId(id);
    }
}
