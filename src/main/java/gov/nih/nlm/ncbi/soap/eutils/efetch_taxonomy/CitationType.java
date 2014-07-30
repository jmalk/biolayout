
package gov.nih.nlm.ncbi.soap.eutils.efetch_taxonomy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CitationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CitationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}CitId"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}CitKey"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}CitUrl" minOccurs="0"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}CitText" minOccurs="0"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}CitPubmedId" minOccurs="0"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}CitMedlineId" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CitationType", propOrder = {
    "citId",
    "citKey",
    "citUrl",
    "citText",
    "citPubmedId",
    "citMedlineId"
})
public class CitationType {

    @XmlElement(name = "CitId", required = true)
    protected String citId;
    @XmlElement(name = "CitKey", required = true)
    protected String citKey;
    @XmlElement(name = "CitUrl")
    protected String citUrl;
    @XmlElement(name = "CitText")
    protected String citText;
    @XmlElement(name = "CitPubmedId")
    protected String citPubmedId;
    @XmlElement(name = "CitMedlineId")
    protected String citMedlineId;

    /**
     * Gets the value of the citId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCitId() {
        return citId;
    }

    /**
     * Sets the value of the citId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCitId(String value) {
        this.citId = value;
    }

    /**
     * Gets the value of the citKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCitKey() {
        return citKey;
    }

    /**
     * Sets the value of the citKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCitKey(String value) {
        this.citKey = value;
    }

    /**
     * Gets the value of the citUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCitUrl() {
        return citUrl;
    }

    /**
     * Sets the value of the citUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCitUrl(String value) {
        this.citUrl = value;
    }

    /**
     * Gets the value of the citText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCitText() {
        return citText;
    }

    /**
     * Sets the value of the citText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCitText(String value) {
        this.citText = value;
    }

    /**
     * Gets the value of the citPubmedId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCitPubmedId() {
        return citPubmedId;
    }

    /**
     * Sets the value of the citPubmedId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCitPubmedId(String value) {
        this.citPubmedId = value;
    }

    /**
     * Gets the value of the citMedlineId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCitMedlineId() {
        return citMedlineId;
    }

    /**
     * Sets the value of the citMedlineId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCitMedlineId(String value) {
        this.citMedlineId = value;
    }

}
