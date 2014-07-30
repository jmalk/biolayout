
package gov.nih.nlm.ncbi.soap.eutils.efetch_taxonomy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NameType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NameType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}ClassCDE"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}DispName"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}UniqueName" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NameType", propOrder = {
    "classCDE",
    "dispName",
    "uniqueName"
})
public class NameType {

    @XmlElement(name = "ClassCDE", required = true)
    protected String classCDE;
    @XmlElement(name = "DispName", required = true)
    protected String dispName;
    @XmlElement(name = "UniqueName")
    protected String uniqueName;

    /**
     * Gets the value of the classCDE property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassCDE() {
        return classCDE;
    }

    /**
     * Sets the value of the classCDE property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassCDE(String value) {
        this.classCDE = value;
    }

    /**
     * Gets the value of the dispName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDispName() {
        return dispName;
    }

    /**
     * Sets the value of the dispName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDispName(String value) {
        this.dispName = value;
    }

    /**
     * Gets the value of the uniqueName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUniqueName() {
        return uniqueName;
    }

    /**
     * Sets the value of the uniqueName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUniqueName(String value) {
        this.uniqueName = value;
    }

}
