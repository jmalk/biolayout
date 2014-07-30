
package gov.nih.nlm.ncbi.soap.eutils.efetch_taxonomy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ModifierType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ModifierType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}ModId"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}ModType"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}ModName"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}ModGBhidden"/>
 *         &lt;choice minOccurs="0">
 *           &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}RModId"/>
 *           &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}RTaxId"/>
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
@XmlType(name = "ModifierType", propOrder = {
    "modId",
    "modType",
    "modName",
    "modGBhidden",
    "rModId",
    "rTaxId"
})
public class ModifierType {

    @XmlElement(name = "ModId", required = true)
    protected String modId;
    @XmlElement(name = "ModType", required = true)
    protected String modType;
    @XmlElement(name = "ModName", required = true)
    protected String modName;
    @XmlElement(name = "ModGBhidden", required = true)
    protected String modGBhidden;
    @XmlElement(name = "RModId")
    protected String rModId;
    @XmlElement(name = "RTaxId")
    protected String rTaxId;

    /**
     * Gets the value of the modId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModId() {
        return modId;
    }

    /**
     * Sets the value of the modId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModId(String value) {
        this.modId = value;
    }

    /**
     * Gets the value of the modType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModType() {
        return modType;
    }

    /**
     * Sets the value of the modType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModType(String value) {
        this.modType = value;
    }

    /**
     * Gets the value of the modName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModName() {
        return modName;
    }

    /**
     * Sets the value of the modName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModName(String value) {
        this.modName = value;
    }

    /**
     * Gets the value of the modGBhidden property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModGBhidden() {
        return modGBhidden;
    }

    /**
     * Sets the value of the modGBhidden property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModGBhidden(String value) {
        this.modGBhidden = value;
    }

    /**
     * Gets the value of the rModId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRModId() {
        return rModId;
    }

    /**
     * Sets the value of the rModId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRModId(String value) {
        this.rModId = value;
    }

    /**
     * Gets the value of the rTaxId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRTaxId() {
        return rTaxId;
    }

    /**
     * Sets the value of the rTaxId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRTaxId(String value) {
        this.rTaxId = value;
    }

}
