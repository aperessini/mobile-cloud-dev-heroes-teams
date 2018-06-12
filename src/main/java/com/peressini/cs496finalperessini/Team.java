package com.peressini.cs496finalperessini;

public class Team {

    private String id;
    private String name;

    public Team(String id, String name) {
        this.id = id;
        this.name = name;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    //to display object as a string in spinner
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Hero){
            Hero h = (Hero )obj;
            if(h.getName().equals(name) && h.getId()==id ) return true;
        }

        return false;
    }

}
