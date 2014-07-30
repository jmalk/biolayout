
package gov.nih.nlm.ncbi.soap.eutils.efetch_taxonomy;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OtherNamesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OtherNamesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}GenbankCommonName" minOccurs="0"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}GenbankAcronym" minOccurs="0"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}BlastName" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}EquivalentName"/>
 *           &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}Synonym"/>
 *           &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}Acronym"/>
 *           &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}Misspelling"/>
 *           &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}Anamorph"/>
 *           &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}Includes"/>
 *           &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}CommonName"/>
 *           &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}Inpart"/>
 *           &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}Misnomer"/>
 *           &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}Teleomorph"/>
 *           &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}GenbankSynonym"/>
 *           &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}GenbankAnamorph"/>
 *           &lt;element name="Name" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}NameType"/>
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
@XmlType(name = "OtherNamesType", propOrder = {
    "genbankCommonName",
    "genbankAcronym",
    "blastName",
    "equivalentNameOrSynonymOrAcronym"
})
public class OtherNamesType {

    @XmlElement(name = "GenbankCommonName")
    protected String genbankCommonName;
    @XmlElement(name = "GenbankAcronym")
    protected String genbankAcronym;
    @XmlElement(name = "BlastName")
    protected String blastName;
    @XmlElementRefs({
        @XmlElementRef(name = "Anamorph", namespace = "http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "Acronym", namespace = "http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "Teleomorph", namespace = "http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "Inpart", namespace = "http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "GenbankSynonym", namespace = "http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "EquivalentName", namespace = "http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "Misspelling", namespace = "http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "Name", namespace = "http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "GenbankAnamorph", namespace = "http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "Misnomer", namespace = "http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "CommonName", namespace = "http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "Includes", namespace = "http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "Synonym", namespace = "http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy", type = JAXBElement.class, required = false)
    })
    protected List<JAXBElement<?>> equivalentNameOrSynonymOrAcronym;

    /**
     * Gets the value of the genbankCommonName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGenbankCommonName() {
        return genbankCommonName;
    }

    /**
     * Sets the value of the genbankCommonName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGenbankCommonName(String value) {
        this.genbankCommonName = value;
    }

    /**
     * Gets the value of the genbankAcronym property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGenbankAcronym() {
        return genbankAcronym;
    }

    /**
     * Sets the value of the genbankAcronym property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGenbankAcronym(String value) {
        this.genbankAcronym = value;
    }

    /**
     * Gets the value of the blastName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBlastName() {
        return blastName;
    }

    /**
     * Sets the value of the blastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBlastName(String value) {
        this.blastName = value;
    }

    /**
     * Gets the value of the equivalentNameOrSynonymOrAcronym property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the equivalentNameOrSynonymOrAcronym property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEquivalentNameOrSynonymOrAcronym().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link NameType }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * 
     */
    public List<JAXBElement<?>> getEquivalentNameOrSynonymOrAcronym() {
        if (equivalentNameOrSynonymOrAcronym == null) {
            equivalentNameOrSynonymOrAcronym = new ArrayList<JAXBElement<?>>();
        }
        return this.equivalentNameOrSynonymOrAcronym;
    }

}
