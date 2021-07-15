/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Import.RealData;

/**
 *
 * @author MaxSondag
 */
public class MetaData {
    
    String attributeName;
    String dataType;
    String valueString;

    public MetaData(String header,String dataType, String valueString) {
        this.attributeName = header;
        this.dataType = dataType;
        this.valueString = valueString;
    }
    
    
}
