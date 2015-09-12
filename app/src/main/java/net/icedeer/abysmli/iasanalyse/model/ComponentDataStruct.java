package net.icedeer.abysmli.iasanalyse.model;

/**
 * Created by Li, Yuan on 21.06.15.
 * All Right reserved!
 */

public class ComponentDataStruct {

    //private variables
    private int _component_id;
    private String _component_name;
    private String _series;
    private String _type;
    private String _component_description;

    // Empty constructor
    public ComponentDataStruct() {

    }

    // constructor
    public ComponentDataStruct(int component_id, String component_name, String series, String type, String component_description) {
        this._component_id = component_id;
        this._component_name = component_name;
        this._series =  series;
        this._type = type;
        this._component_description = component_description;
    }

    // constructor
    public ComponentDataStruct(String component_name, String series, String type, String component_description) {
        this._component_name = component_name;
        this._series =  series;
        this._type = type;
        this._component_description = component_description;
    }

    // getting COMPONENT ID
    public int get_component_id() {
        return this._component_id;
    }

    // setting COMPONENT ID
    public void set_component_id(int component_id) {
        this._component_id = component_id;
    }

    // getting COMPONENT NAME
    public String get_component_name() {
        return this._component_name;
    }

    // setting COMPONENT NAME
    public void set_component_name(String component_name) {
        this._component_name = component_name;
    }

    // getting series
    public String get_series() {
        return this._series;
    }

    // setting series
    public void set_series(String series) {
        this._series = series;
    }

    // getting type
    public String get_type() {
        return this._type;
    }

    // setting type
    public void set_type(String type) {
        this._type = type;
    }

    // getting component_description
    public String get_component_description() {
        return this._component_description;
    }

    // setting component_description
    public void set_component_description(String component_description) {
        this._component_description = component_description;
    }

}
