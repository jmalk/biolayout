
package gov.nih.nlm.ncbi.soap.eutils.efetch_taxonomy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GeneticCodeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GeneticCodeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}GCId"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}GCName"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GeneticCodeType", propOrder = {
    "gcId",
    "gcName"
})
public class GeneticCodeType {

    @XmlElement(name = "GCId", required = true)
    protected String gcId;
    @XmlElement(name = "GCName", required = true)
    protected String gcName;

    /**
     * Gets the value of the gcId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGCId() {
        return gcId;
    }

    /**
     * Sets the value of the gcId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGCId(String value) {
        this.gcId = value;
    }

    /**
     * Gets the value of the gcName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGCName() {
        return gcName;
    }

    /**
     * Sets the value of the gcName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGCName(String value) {
        this.gcName = value;
    }

}
