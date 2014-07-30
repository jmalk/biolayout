
package gov.nih.nlm.ncbi.soap.eutils.efetch_taxonomy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}PropName"/>
 *         &lt;choice>
 *           &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}PropValueInt"/>
 *           &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}PropValueBool"/>
 *           &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}PropValueString"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PropertyType", propOrder = {
    "propName",
    "propValueInt",
    "propValueBool",
    "propValueString"
})
public class PropertyType {

    @XmlElement(name = "PropName", required = true)
    protected String propName;
    @XmlElement(name = "PropValueInt")
    protected String propValueInt;
    @XmlElement(name = "PropValueBool")
    protected String propValueBool;
    @XmlElement(name = "PropValueString")
    protected String propValueString;

    /**
     * Gets the value of the propName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPropName() {
        return propName;
    }

    /**
     * Sets the value of the propName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPropName(String value) {
        this.propName = value;
    }

    /**
     * Gets the value of the propValueInt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPropValueInt() {
        return propValueInt;
    }

    /**
     * Sets the value of the propValueInt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPropValueInt(String value) {
        this.propValueInt = value;
    }

    /**
     * Gets the value of the propValueBool property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPropValueBool() {
        return propValueBool;
    }

    /**
     * Sets the value of the propValueBool property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPropValueBool(String value) {
        this.propValueBool = value;
    }

    /**
     * Gets the value of the propValueString property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPropValueString() {
        return propValueString;
    }

    /**
     * Sets the value of the propValueString property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPropValueString(String value) {
        this.propValueString = value;
    }

}
