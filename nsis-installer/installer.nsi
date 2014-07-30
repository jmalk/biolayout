!include "MUI2.nsh"
!include "x64.nsh"
!include "FileAssociation.nsh"
!include "LogicLib.nsh"

!addplugindir "."

!define LONG_NAME "BioLayout Express 3D"
!define VERSION "_VERSION_"
!define BASE_NAME "BLE3D"
!define BASE_DIR ".."

!define INSTALLER_NAME "${BASE_NAME}-_VERSION_-installer.exe"
!define 32BIT_EXE_NAME "${BASE_NAME}-_VERSION_-32bit.exe"
!define 64BIT_EXE_NAME "${BASE_NAME}-_VERSION_-64bit.exe"
!define OUTPUT_EXE_NAME "${BASE_NAME}.exe"
!define OLD_EXE_NAME "BioLayoutExpress3D.exe"

; General
Name "${LONG_NAME}"
OutFile "${INSTALLER_NAME}"
InstallDir "$PROGRAMFILES\${LONG_NAME}"
InstallDirRegKey HKCU "Software\${LONG_NAME}" ""

; Product & Version Information
VIProductVersion "1.0.0.0"

VIAddVersionKey ProductName "${LONG_NAME}"
VIAddVersionKey Comments "${LONG_NAME}"
VIAddVersionKey LegalCopyright "� The University of Edinburgh, European Molecular Biology Laoratory, Wellcome Trust Sanger Institue, Genome Research Ltd. 2006-2014"
VIAddVersionKey FileDescription "${LONG_NAME}"
VIAddVersionKey FileVersion "1.0.0.0"
VIAddVersionKey ProductVersion "1.0.0.0"

; Installer Icons
!insertmacro MUI_DEFAULT MUI_ICON "${BASE_DIR}/src/main/resources/Resources/Images/BioLayoutExpress3DIcon.ico"
!insertmacro MUI_DEFAULT MUI_UNICON "${BASE_DIR}/src/main/resources/Resources/Images/BioLayoutExpress3DIcon.ico"

Icon "${MUI_ICON}"
UninstallIcon "${MUI_UNICON}"

WindowIcon on

; Variables
Var MUI_TEMP
Var STARTMENU_FOLDER

; Pages
!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_LICENSE "Licenses.txt"

!define MUI_COMPONENTSPAGE_NODESC
!insertmacro MUI_PAGE_COMPONENTS

!insertmacro MUI_PAGE_DIRECTORY

; Start Menu Folder Page Configuration
!define MUI_STARTMENUPAGE_REGISTRY_ROOT "HKCU"
!define MUI_STARTMENUPAGE_REGISTRY_KEY "Software\${LONG_NAME}"
!define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "Start Menu Folder"

!insertmacro MUI_PAGE_STARTMENU ${BASE_NAME} $STARTMENU_FOLDER

!insertmacro MUI_PAGE_INSTFILES

!define MUI_FINISHPAGE_RUN
!define MUI_FINISHPAGE_RUN_CHECKED
!define MUI_FINISHPAGE_RUN_TEXT "Start ${LONG_NAME}"
!define MUI_FINISHPAGE_RUN_FUNCTION "Launch"

!insertmacro MUI_PAGE_FINISH

!insertmacro MUI_UNPAGE_WELCOME
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES
!insertmacro MUI_UNPAGE_FINISH

;Launch function
Function Launch
    File "ShellExecAsUser.dll"

    ShellExecAsUser::ShellExecAsUser "" "$INSTDIR\${OUTPUT_EXE_NAME}"
FunctionEnd

Function CheckJVM
    Var /GLOBAL JVM_BITNESS

    File DetectJVM.exe
    ClearErrors
    nsExec::Exec "$INSTDIR\DetectJVM.exe"
    Pop $0
    IfErrors DetectExecError
    IntCmp $0 0 DetectError DetectError DoneDetect
    DetectExecError:
        StrCpy $0 "exec error"
    DetectError:
        MessageBox MB_OK "Could not determine JVM architecture ($0). Assuming 32-bit."
        Goto NotX64
    DoneDetect:
    IntCmp $0 64 X64 NotX64 NotX64
    X64:
        StrCpy $JVM_BITNESS "64"
        Goto DoneX64
    NotX64:
        StrCpy $JVM_BITNESS "32"
    DoneX64:
    Delete $INSTDIR\DetectJvm.exe
FunctionEnd

;Languages
!insertmacro MUI_LANGUAGE "English"

; Main asset
Section "-${LONG_NAME}"

    SetOutPath "$INSTDIR"

    Call CheckJVM

    ${If} $JVM_BITNESS = '64'
        File "/oname=${OUTPUT_EXE_NAME}" "${BASE_DIR}/target/${64BIT_EXE_NAME}"
    ${Else}
        File "/oname=${OUTPUT_EXE_NAME}" "${BASE_DIR}/target/${32BIT_EXE_NAME}"
    ${Endif}

    File "Licenses.txt"

    WriteRegStr HKLM "SOFTWARE\${LONG_NAME}" "Install_Dir" "$INSTDIR"

    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${LONG_NAME}" "DisplayName" "${LONG_NAME}"
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${LONG_NAME}" "UninstallString" '"$INSTDIR\uninstall.exe"'
    WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${LONG_NAME}" "NoModify" 1
    WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${LONG_NAME}" "NoRepair" 1

    WriteUninstaller "$INSTDIR\Uninstall.exe"
    Delete "$INSTDIR\${OLD_EXE_NAME}"

    !insertmacro MUI_STARTMENU_WRITE_BEGIN ${BASE_NAME}

    CreateDirectory "$SMPROGRAMS\$STARTMENU_FOLDER\"
    CreateShortCut "$SMPROGRAMS\$STARTMENU_FOLDER\Uninstall.lnk" "$INSTDIR\Uninstall.exe"
    CreateShortCut "$SMPROGRAMS\$STARTMENU_FOLDER\${LONG_NAME}.lnk" "$INSTDIR\${OUTPUT_EXE_NAME}"

    !insertmacro MUI_STARTMENU_WRITE_END

SectionEnd

; File Associations
SectionGroup "File associations"
    Section "BioLayout layout file (.layout)"
        ${RegisterExtension} "$INSTDIR\${OUTPUT_EXE_NAME}" ".layout" "BioLayout Express 3D Layout File"
    SectionEnd

    Section "Simple interaction file (.sif)"
        ${RegisterExtension} "$INSTDIR\${OUTPUT_EXE_NAME}" ".sif" "BioLayout Express 3D Sif File"
    SectionEnd

    Section "Gene expression file (.expression)"
        ${RegisterExtension} "$INSTDIR\${OUTPUT_EXE_NAME}" ".expression" "BioLayout Express 3D Expression File"
    SectionEnd

    Section "Matrix file (.matrix)"
        ${RegisterExtension} "$INSTDIR\${OUTPUT_EXE_NAME}" ".matrix" "BioLayout Express 3D Matrix File"
    SectionEnd
SectionGroupEnd

; Desktop shortcut
Section "Desktop shortcut"
    !insertmacro MUI_STARTMENU_WRITE_BEGIN ${BASE_NAME}
    CreateShortCut "$DESKTOP\${LONG_NAME}.lnk" "$INSTDIR\${OUTPUT_EXE_NAME}"
    !insertmacro MUI_STARTMENU_WRITE_END
SectionEnd

; Uninstaller
Section "Uninstall"

    Delete "$INSTDIR\${OUTPUT_EXE_NAME}"
    Delete "$INSTDIR\Licenses.txt"

    Delete "$INSTDIR\Uninstall.exe"

    RMDir /r "$INSTDIR"

    !insertmacro MUI_STARTMENU_GETFOLDER ${BASE_NAME} $MUI_TEMP

    Delete "$SMPROGRAMS\$MUI_TEMP\Uninstall.lnk"
    Delete "$SMPROGRAMS\$MUI_TEMP\${LONG_NAME}.lnk"

    RMDir /r "$SMPROGRAMS\$MUI_TEMP"

    Delete "$DESKTOP\${LONG_NAME}.lnk"

    DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${LONG_NAME}"
    DeleteRegKey HKLM "SOFTWARE\${LONG_NAME}"

    ${UnregisterExtension} ".layout"     "BioLayout Express 3D Layout File"
    ${UnregisterExtension} ".sif"        "BioLayout Express 3D Sif File"
    ${UnregisterExtension} ".expression" "BioLayout Express 3D Expression File"
    ${UnregisterExtension} ".matrix"     "BioLayout Express 3D Matrix File"

SectionEnd
