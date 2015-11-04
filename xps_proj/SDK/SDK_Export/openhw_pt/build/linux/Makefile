ifndef PETALINUX
$(error "Error: PETALINUX environment variable not set.  Change to the root of your PetaLinux install, and source the settings.sh file")
endif

include $(PETALINUX)/etc/logging/logging.mk
include $(PETALINUX)/etc/build/plnx-common-functions.mk

dir_kernel_y:=
dir_u_boot_y:=
dir_rootfs_y:=
dir_generic_y:=
dir_hw_description_y:=
include Makefile.autoinclude

ifndef PROOT
$(error "PetaLinux project is not specified!")
endif

ifndef INSTANCE
$(error "Subsystem instance is not defined!")
endif

ifeq ($(SUBSYSTEM),)
$(error "Failed to build $(INSTANCE)! no Subsystem is specified!")
endif

ifeq ($(SUBSYSTEM_CONFIG),)
$(error "Failed to build $(INSTANCE)! no Subsystem configuration file is specified!")
endif

dir_y:=$(dir_kernel_y) $(dir_u_boot_y) $(dir_rootfs_y) $(dir_hw_description_y) $(dir_generic_y)

KCONFIGFILE=$(CURDIR)/Kconfig
TYPE:=subsystem

all: pre-build build post-build pre-install install post-install package
.PHONY: pre-config config post-config pre-build build post-build pre-install install post-install package

SUB_DO:=$(addprefix sub_post-config_component_,$(dir_y))
SUB_DO+=$(addprefix sub_pre-build_component_,$(dir_y))
SUB_DO+=$(addprefix sub_build_component_,$(dir_y))
SUB_DO+=$(addprefix sub_post-build_component_,$(dir_y))
SUB_DO+=$(addprefix sub_pre-install_component_,$(dir_y))
SUB_DO+=$(addprefix sub_install_component_,$(dir_y))
SUB_DO+=$(addprefix sub_post-install_component_,$(dir_y))
SUB_DO+=$(addprefix sub_clean_component_,$(dir_y))

$(addprefix sub_build_component_,$(dir_y)):build_hw-description

config_hw-description:
	$(MAKE) INSTANCE=$(INSTANCE)/hw-description SUBSYSTEM=$(INSTANCE) -C hw-description config

build_hw-description:
	$(MAKE) INSTANCE=$(INSTANCE)/hw-description SUBSYSTEM=$(INSTANCE) -C hw-description build
install_hw-description:
	$(MAKE) INSTANCE=$(INSTANCE)/hw-description SUBSYSTEM=$(INSTANCE) -C hw-description install

$(SUB_DO):
	$(eval TARGET:=$(subst sub_,,$(firstword $(subst _component_, ,$@))))
	$(eval SUBCOMPONENT:=$(subst sub_$(TARGET)_component_,,$@))
	$(eval SINSTANCE:=$(shell echo $@ | awk -F "/" '{print $$2}'))
	$(eval STYPE:=$(shell echo $@ | awk -F "/" '{print $$3}'))
	$(eval SMULTI:=$(shell echo $@ | awk -F "/" '{print $$4}'))
	$(eval SNAME:=$(shell echo $@ | awk -F "/" '{print $$5}'))
	if [ ! -z "$(SINSTANCE)" ] && [ ! "$(SINSTANCE)" == "none" ]; then \
		sinstance=$(SINSTANCE); \
		sbuild=$(SINSTANCE); \
	elif [ "$(SMULTI)" == "multi" ]; then \
		sinstance=$(SNAME); \
		sbuild=$(STYPE)/$(SNAME); \
	else \
		sinstance=$(STYPE); \
		sbuild=$(STYPE); \
	fi; \
	$(MAKE) INSTANCE=$(INSTANCE)/$${sinstance} -C $${sbuild} $(TARGET)

oldconfig: $(KCONFIGFILE)
	$(call do_info,"$@ $(INSTANCE)")
	$(call plnx_qpushd,$(PROOT)); yes "" | env KCONFIG_CONFIG="$(SUBSYSTEM_CONFIG)" conf "$(KCONFIGFILE)" || exit 255; $(call plnx_qpopd)

config: $(KCONFIGFILE)
	$(call do_info,"$@ $(INSTANCE)")
	env KCONFIG_CONFIG="$(SUBSYSTEM_CONFIG)" mconf "$(KCONFIGFILE)" || exit 255

.PHONY: $(KCONFIGFILE)
pre-config $(KCONFIGFILE):
	#generate Kconfig file
	@(echo "mainmenu \"$(INSTANCE) Configuration\""; \
	echo "config SUBSYSTEM_TYPE_LINUX"; \
	echo "	bool"; \
	echo "	default y"; \
	echo "comment \"$(INSTANCE) Components Selection\""; \
	$(call generate_component_config,$(TYPE),linux-kernel,kernel,y,,n,); \
	$(call generate_component_config,$(TYPE),u-boot,,y,,n,); \
	$(call generate_component_config,$(TYPE),rootfs,,n,,n,); \
	echo "menu \"Generic Components\""; \
	$(call generate_component_config_multi,$(TYPE),generic,,); \
	echo "endmenu"; \
	cat Kconfig.part) > "Kconfig"

post-config:
	petalinux-fix-configs --subsystem $(PROOT)/subsystems/$(INSTANCE)
	$(MAKE) INSTANCE=$(INSTANCE)/hw-description SUBSYSTEM=$(INSTANCE) -C hw-description config || exit 255

pre-build:| $(addprefix sub_pre-build_component_,$(dir_y))
build:| build_hw-description $(addprefix sub_build_component_,$(dir_y))
post-build: | $(addprefix sub_post-build_component_,$(dir_y))
pre-install: | $(addprefix sub_pre-install_component_,$(dir_y))
install: | install_hw-description $(addprefix sub_install_component_,$(dir_y))
post-install: | $(addprefix sub_post-install_component_,$(dir_y))
clean: | $(addprefix sub_clean_component,$(dir_y))

package:
	make INSTANCE=$(SUBSYSTEM) SUBSYSTEM=$(SUBSYSTEM) -f Makefile.package package-subsystem

package-%:
	make INSTANCE=$(SUBSYSTEM) SUBSYSTEM=$(SUBSYSTEM) -f Makefile.package $@

distclean:

mrproper:

help:
	@echo ""
	@echo "Quick reference for various supported build targets for $(INSTANCE)."
	@echo "----------------------------------------------------"
	@echo "  clean                  clean out build objects"
	@echo "  distclean              clean out build"
	@echo "  all                    build subsystem and generate final image"
	@echo "  build                  build subsystem"
	@echo "  build_hw-description   build hw-description"
	@echo "  install_hw-description install dtb to subsystem images directory"
	@echo "  install                install built objects to target subsystem host copy"
	@echo "  package                combine target file system and kernel into final image"
