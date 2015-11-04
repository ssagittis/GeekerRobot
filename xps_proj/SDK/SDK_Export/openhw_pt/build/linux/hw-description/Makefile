ifndef PETALINUX
$(error "Error: PETALINUX environment variable not set.  Change to the root of your PetaLinux install, and source the settings.sh file")
endif

include $(PETALINUX)/etc/logging/logging.mk

TYPE:=hw-description

ifndef PROOT
$(error "Failed to build $(TYPE). No PetaLinux project is specified")
endif

ifndef INSTANCE
$(error "Failed to build $(TYPE). No instance specified")
endif

ifndef SUBSYSTEM
$(error "Failed to build $(INSTANCE). No subsystem is specified."
endif

include $(PROOT)/build/$(SUBSYSTEM)/Makefile.export

ifndef IMAGEROOT
$(error "Failed to build $(INSTANCE). No image directory is specified")
endif

ifndef SUBSYSTEM_CONFIG
$(error "Failed to build $(INSTANCE). No subsystem configuration file is specified")
endif

dir_hw_description_y:=$(PROOT)/subsystems/$(SUBSYSTEM)/hw-description

dir_y:=$(dir_hw_description_y)

.PHONY: 

all: build install

pre-config:

.PHONY: config
config: $(dir_y)/system.dts $(SUBSYSTEM_CONFIG)
	@$(call do_info, "generate $(dir_y)/system.dts")
	petalinux-update-dts -c "$(SUBSYSTEM_CONFIG)" -i "$(dir_y)/system.dts" -o "$(dir_y)/system.dts" || exit 255

post-config:

pre-build:

build: system.dtb

system.dtb: $(dir_y)/system.dts
	@$(call do_info, "build $@")
	builddir=$(CURDIR); \
	pushd $(dir_y)/ 1>/dev/null; \
	dtc -O dtb -o "$${builddir}/$@" -b 0 -p 1024 $< || exit 255; \
	popd 1>/dev/null

$(IMAGEROOT):
	mkdir -p $@

post-build:

pre-install:

install: system.dtb $(IMAGEROOT)
	rsync system.dtb "$(IMAGEROOT)"/

post-install:

clean:
	rm -rf system.dts system.dtb

mrproper distclean:

help:
	@echo ""
	@echo "Quick reference for various supported build targets for $(INSTANCE)."
	@echo "----------------------------------------------------"
	@echo "  clean          clean out compiled files"
	@echo "  mrproper       clean out all non-distributed files"
	@echo "  distclean      clean out all non-distributed files"
	@echo "  build          build $(INSTANCE)"
	@echo "  all            build $(INSTANCE) and copy DTB to images/ directory"
