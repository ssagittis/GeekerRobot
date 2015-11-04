ifndef PETALINUX
$(error "Error: PETALINUX environment variable not set.  Change to the root of your PetaLinux install, and source the settings.sh file")
endif

TYPE:=u-boot

dir_u_boot_y:=

include Makefile.autoinclude

ifndef PROOT
$(error "Failed to build $(TYPE). No PetaLinux project is specified")
endif

ifndef INSTANCE
$(error "Failed to build $(TYPE). No instance is specified")
endif

ifndef CONFIGROOT
$(error "Failed to build $(INSTANCE). No configuration root directory is specified")
endif

ifndef IMAGEROOT
$(error "Failed to build $(INSTANCE). No image directory is specified")
endif

ifndef SUBSYSTEM_CONFIG
$(error "Failed to build $(INSTANCE). No subsystem configuration file is specified")
endif

ifndef HW_DESCRIPTION
$(error "Failed to build $(INSTANCE). No hw-description is sepecified")
endif

include $(PETALINUX)/etc/build/common.mk

ifndef ARCH
$(error "Failed to build $(INSTANCE). No ARCH specified!")
endif

ifndef ENDIAN
$(error "Failed to build $(INSTANCE). No endian specified")
endif

dir_y:=$(dir_u_boot_y)
ifeq ($(dir_y),)
$(error "Failed to build U-Boot. No selected U-Boot")
endif


UBOOTSRC:=$(firstword $(dir_y))
BUILDROOT:=$(CURDIR)
UBOOTOUT:=$(BUILDROOT)/$(shell basename $(UBOOTSRC))

include $(PETALINUX)/etc/build/common.mk

UBOOT_VENDOR=xilinx
ifeq ($(ARCH),microblaze)
UBOOT_PLATFORM=microblaze-generic
UBOOT_BOARD=microblaze-generic
else
ifeq ($(ARCH),arm)
UBOOT_PLATFORM=zynq_zc70x
UBOOT_BOARD=zynq
else
$(error "Failed to build $(INSTANCE), unknown ARCH: $(ARCH)")
endif
endif

UBOOT_CONFIG=$(shell grep "^CONFIG_SUBSYSTEM_UBOOT_CONFIG_TARGET=" $(SUBSYSTEM_CONFIG) | cut -d "=" -f 2)

ifneq ($(UBOOT_CONFIG),)
UBOOT_PLATFORM:=$(subst ",,$(UBOOT_CONFIG))
endif

UBOOTBUILDSRC=$(BUILDROOT)/src/$(shell basename $(UBOOTSRC))
UBOOT_BOARD_DIR=$(UBOOTBUILDSRC)/board/$(UBOOT_VENDOR)/$(UBOOT_BOARD)
UBOOT_GENERIC_CONFIG_H=petalinux-$(ARCH)-auto.h

UBOOT_CONFIG_PLNX_TEMPLATE=$(PETALINUX)/components/u-boot/petalinux-auto-board.h.template
UBOOT_CONFIG_PLNX=$(UBOOTBUILDSRC)/include/configs/petalinux-auto-board.h

UBOOT_CONFIG_USER_TEMPLATE=$(CONFIGROOT)/$(lastword $(subst /, ,$(INSTANCE)))/petalinux-user-config.h.template
UBOOT_CONFIG_USER=$(UBOOTBUILDSRC)/include/configs/petalinux-user-config.h
UBOOT_CONFIG_USER_SCRIPT=$(CONFIGROOT)/$(lastword $(subst /, ,$(INSTANCE)))/petalinux-user-config-script

# We need to crack some settings out of the user/vendor .config file
# This is a bit hacky, a more generic system would be nice

USE_DHCP=$(shell grep "CONFIG_SUBSYSTEM_USE_DHCP" $(SUBSYSTEM_CONFIG) \
		2</dev/null | cut -d "=" -f 2 | grep -v "^\#")

MAKEUBOOT=$(MAKE) O=$(UBOOTOUT) -C $(UBOOTBUILDSRC) -j$(HOST_NCPU)

export UBOOTBUILDSRC UBOOT_BOARD_DIR UBOOT_CONFIG_PLNX_TEMPLATE UBOOT_CONFIG_PLNX UBOOT_CONFIG_USER_TEMPLATE UBOOT_CONFIG_USER UBOOT_CONFIG_USER_SCRIPT USE_DHCP


.PHONY: all build pre-build post-build config pre-config post-config clean distclean mrproper

all: build post-build

oldconfig:
config:

pre-build:

build:$(UBOOTOUT)/u-boot

post-build:

$(UBOOTOUT)/include/config.h:$(UBOOTBUILDSRC) 
	@$(call do_info,"generate $(INSTANCE) configuration files")
ifeq ($(UBOOT_CONFIG),)
	if ! grep -q "$(UBOOT_GENERIC_CONFIG_H)" "$(UBOOTOUT)"/include/config.h 2>/dev/null ; then \
		$(MAKE) O=$(UBOOTOUT) -C $(UBOOTBUILDSRC) -j$(HOST_NCPU) $(UBOOT_PLATFORM)_config || exit 255; \
		sed  -i -e "s%[[:space:]]*#include.*$(UBOOT_PLATFORM).*%#include <configs/$(UBOOT_GENERIC_CONFIG_H)>%g" "$(UBOOTOUT)"/include/config.h; \
	fi
else
	$(MAKE) O=$(UBOOTOUT) -C $(UBOOTBUILDSRC) -j$(HOST_NCPU) $(UBOOT_PLATFORM)_config || exit 255
endif

clean:
	@$(call do_info, "$@ $(INSTANCE)")
	$(MAKEUBOOT) $@

mrproper distclean:

$(UBOOTOUT)/u-boot: $(UBOOTBUILDSRC) $(UBOOTOUT)/include/config.h
	@$(call do_info,"build $(INSTANCE)")
	$(MAKEUBOOT) all

.PHONY: $(UBOOTBUILDSRC)
$(UBOOTBUILDSRC): $(UBOOTSRC)
	@$(call do_info, "update $(INSTANCE) source")
	if [ ! -d "$(UBOOTBUILDSRC)" ]; then \
		mkdir -p "$(UBOOTBUILDSRC)"; \
	fi; \
	rsync -a --cvs-exclude --filter=':C .git/' "$(UBOOTSRC)"/ "$(UBOOTBUILDSRC)"/
ifeq ($(UBOOT_CONFIG),)
	chmod -R a+w "$(UBOOTBUILDSRC)"/include/configs
	if [ -f "$(PROOT)/subsystems/$(HW_DESCRIPTION)/config.mk" ]; then \
		rsync "$(PROOT)/subsystems/$(HW_DESCRIPTION)/config.mk"  "$(UBOOT_BOARD_DIR)"/; \
	fi; \
	if [ -f "$(PROOT)/subsystems/$(HW_DESCRIPTION)/xparameters.h" ]; then \
		rsync "$(PROOT)/subsystems/$(HW_DESCRIPTION)/xparameters.h"  "$(UBOOT_BOARD_DIR)"/; \
	fi
	rsync "$(PETALINUX)/components/u-boot/$(UBOOT_GENERIC_CONFIG_H)" "$(UBOOTBUILDSRC)"/include/configs/
	petalinux-uboot-config
endif

pre-install:
install:$(IMAGEROOT)
	@$(call do_info,"$@ $(INSTANCE)")
	if [ ! -f $(UBOOTOUT)/u-boot ]; then \
		echo "ERROR: No built u-boot image is found!"; \
		exit 255; \
	fi; \
	cp $(UBOOTOUT)/u-boot.srec $(IMAGEROOT); \
	cp $(UBOOTOUT)/u-boot.bin $(IMAGEROOT); \
	cp $(UBOOTOUT)/u-boot $(IMAGEROOT)/u-boot.elf
post-install:

.PHONY: %_only
%_only: $(UBOOTBUILDSRC)
	$(eval TARGET:=$(subst _only,,$@))
	@$(call do_info,"$(TARGET) $(INSTANCE)")
	$(MAKEUBOOT) $(TARGET)

$(IMAGEROOT):
	mkdir -p "$@"

help:
	@echo ""
	@echo "Quick reference for various supported build targets for u-boot."
	@echo "----------------------------------------------------"
	@echo "  clean               clean out compiled files, but not config"
	@echo "  distclean           clean out all build files"
	@echo "  build               build u-boot"
	@echo "  install               install u-boot images to images/ directory"
	@echo "  <UBOOT_TARGET>_only make a specific u-boot target"
	@echo "  all                 configure build environment and build"
