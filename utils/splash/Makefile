FONT=LiberationSans-Bold.ttf
FONT_ITALIC=LiberationSans-BoldItalic.ttf
VERSION_TXT=version.txt

STATIC_SRC= \
	background.png \
	graph.png \
	\
	uoe.png \
	roslin.png \
	embl-ebi.png

GENERATED_DIR=generated
GENERATED_SRC= \
	$(GENERATED_DIR)/biolayout.png \
	$(GENERATED_DIR)/express.png \
	$(GENERATED_DIR)/3d.png \
	$(GENERATED_DIR)/credits.png \
	$(GENERATED_DIR)/version.png \
	$(GENERATED_DIR)/$(VERSION_TXT)

TARGET=$(GENERATED_DIR)/splash.png

SRC= \
	$(STATIC_SRC) \
	$(GENERATED_SRC)

SHADOW_PARAMS=\( +clone -background black -shadow 80x4+8+8 \) \
    +swap -background none -layers merge +repage

CONVERT=$(IM_HOME)convert

LAST_VERSION=$(shell cat $(GENERATED_DIR)/$(VERSION_TXT))
all:
	@mkdir -p $(GENERATED_DIR)
ifneq ($(VERSION),)
ifneq ($(LAST_VERSION),$(VERSION))
	@echo $(VERSION) > $(GENERATED_DIR)/$(VERSION_TXT)
endif
endif
	@$(MAKE) $(TARGET) # Do the actual build

$(GENERATED_DIR)/biolayout.png:
	$(CONVERT) -background none -fill white \
		-font $(FONT) -pointsize 265 label:'BioLayout' \
		$(SHADOW_PARAMS) $@

$(GENERATED_DIR)/express.png:
	$(CONVERT) -background none -fill white \
		-font $(FONT_ITALIC) -pointsize 265 label:'Express' \
		$(SHADOW_PARAMS) $@

$(GENERATED_DIR)/3d.png:
	$(CONVERT) -background none -fill white \
		-font $(FONT) -pointsize 165 label:'3D' \
		$(SHADOW_PARAMS) $@

$(GENERATED_DIR)/credits.png: credits.txt
	$(CONVERT) -background none -fill white \
		-font $(FONT) -pointsize 46 label:"$(shell cat $<)" \
		$(SHADOW_PARAMS) $@

$(GENERATED_DIR)/$(VERSION_TXT):
ifneq ($(VERSION),)
	echo $(VERSION) > $(GENERATED_DIR)/$(VERSION_TXT)
else
	echo "undefined" > $(GENERATED_DIR)/$(VERSION_TXT)
endif

$(GENERATED_DIR)/version.png: $(GENERATED_DIR)/$(VERSION_TXT)
	$(CONVERT) -background none -fill white \
		-font $(FONT) -pointsize 46 label:"Version $(shell cat $<)" \
		$(SHADOW_PARAMS) $@

$(TARGET): $(SRC) Makefile
	$(CONVERT) -size 2631x1379 xc:none \
		background.png -geometry +0+0       -composite \
		graph.png      -geometry +0+0       -composite \
		uoe.png        -geometry +13+11     -composite \
		roslin.png     -geometry +264+43    -composite \
		embl-ebi.png   -geometry +58+266    -composite \
		$(GENERATED_DIR)/biolayout.png  -geometry +15+550    -composite \
		$(GENERATED_DIR)/express.png    -geometry +15+770    -composite \
		$(GENERATED_DIR)/3d.png         -geometry +1070+770  -composite \
		$(GENERATED_DIR)/credits.png    -geometry +15+15     -gravity SouthWest -composite \
		$(GENERATED_DIR)/version.png    -geometry +10+10     -gravity NorthEast -composite \
		$(TARGET)

clean:
	rm -f $(GENERATED_SRC) $(TARGET)

install: all
	$(CONVERT) -resize 750x393 $(TARGET) ../../src/main/resources/Resources/Images/BioLayoutExpress3DLogo.png

.PHONY: clean all
