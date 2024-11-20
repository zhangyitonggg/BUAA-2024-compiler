package backend.data;

import backend.data.Data;

/**
 * label1:
 */
public class Label extends Data {
    public Label(String name) {
        super(name);
    }
    
    
    @Override
    public String toString() {
        return name + ':';
    }
}
