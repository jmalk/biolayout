
package gov.nih.nlm.ncbi.soap.eutils.efetch_taxonomy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MitoGeneticCodeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MitoGeneticCodeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}MGCId"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}MGCName"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MitoGeneticCodeType", propOrder = {
    "mgcId",
    "mgcName"
})
public class MitoGeneticCodeType {

    @XmlElement(name = "MGCId", required = true)
    protected String mgcId;
    @XmlElement(name = "MGCName", required = true)
    protected String mgcName;

    /**
     * Gets the value of the mgcId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMGCId() {
        return mgcId;
    }

    /**
     * Sets the value of the mgcId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMGCId(String value) {
        this.mgcId = value;
    }

    /**
     * Gets the value of the mgcName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMGCName() {
        return mgcName;
    }

    /**
     * Sets the value of the mgcName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMGCName(String value) {
        this.mgcName = value;
    }

}
