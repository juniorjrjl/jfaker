package net.jfaker.model;

public class DogModel extends AnimalModel {

    private boolean docile;

    public void setId(final long id){
        this.id = id;
    }

    public boolean isDocile() {
        return docile;
    }

    public void setDocile(final boolean docile) {
        this.docile = docile;
    }

}
