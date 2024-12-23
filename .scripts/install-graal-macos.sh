#! /bin/bash

set -eu

# Get the macOS version
macos_version=$(sw_vers -productVersion)

# Extract the major version (e.g., 12, 13, 14)
major_version=$(echo "$macos_version" | cut -d '.' -f 1)

echo ">>> macOS.major_version=$major_version"

# Set the app_bin_suffix based on the macOS major version using case
case "$major_version" in
  12)
    app_bin_suffix="macos12"
    ;;
  13)
    app_bin_suffix="macos-13"
    ;;
  14 | 15)
    # Get the system architecture
    arch=$(uname -m)

    echo ">>> macOS.arch=${arch}"

    # Set the app_bin_suffix based on the architecture
    if [ "$arch" == "x86_64" ]; then
      app_bin_suffix="macos-13"
    elif [ "$arch" == "arm64" ]; then
      app_bin_suffix="macos-${major_version}-arm64"
    else
      app_bin_suffix="macos-13"
    fi
    ;;
  *)
    app_bin_suffix="macos-13"
    echo "Unsupported macOS version: $macos_version so it will use $app_bin_suffix "
    ;;
esac

app_executable_name=maven2sbt
app_name=maven2sbt-cli
app_version=${1:-1.5.0}
app_package_file="${app_name}-macos-latest"
download_url="https://github.com/kevin-lee/maven2sbt/releases/download/v${app_version}/${app_package_file}-${app_bin_suffix}"

usr_local_path=$HOME
opt_location="${usr_local_path}/opt"
app_location="${opt_location}/${app_name}"
installed_app_bin_path="${app_location}/${app_executable_name}"
usr_local_bin_path="${usr_local_path}/bin"
app_bin_path="${usr_local_bin_path}/${app_executable_name}"

echo "--------------------------------------------------------------------------------"
echo "app_executable_name = ${app_executable_name}"
echo "           app_name = ${app_name}"
echo "     app_bin_suffix = ${app_bin_suffix}"
echo "        app_version = ${app_version}"
echo "   app_package_file = ${app_package_file}"
echo "       download_url = ${download_url}"
echo "--------------------------------------------------------------------------------"
echo "        usr_local_path = ${usr_local_path}"
echo "          opt_location = ${opt_location}"
echo "          app_location = ${app_location}"
echo "installed_app_bin_path = ${installed_app_bin_path}"
echo "    usr_local_bin_path = ${usr_local_bin_path}"
echo "          app_bin_path = ${app_bin_path}"
echo "--------------------------------------------------------------------------------"

cd /tmp

curl -Lo $app_package_file $download_url

ls -l $app_package_file || { echo "maven2sbt version ${app_version} doesn't seem to exist." && false ; }
chmod ug+x $app_package_file

mkdir -p $opt_location
mkdir -p $usr_local_bin_path

rm -R $app_location || true
mkdir -p $app_location
mv $app_package_file $installed_app_bin_path

echo ""
{ rm $app_bin_path && { echo "The existing $app_bin_path was found so it was removed." ; } } || { echo "No existing $app_bin_path was found. It's OK. Please ignore the 'No such file or directory' message." ; }
echo ""
echo "ln -s $installed_app_bin_path $app_bin_path"
ln -s $installed_app_bin_path $app_bin_path || true

current_shell="$SHELL"

echo ""
if [[ $current_shell == *zsh ]]; then
  { echo $PATH | grep -q "${usr_local_bin_path}" ; } || { echo "export PATH=${usr_local_bin_path}"':$PATH' >> ~/.zshrc ; echo "${usr_local_bin_path} is not found in PATH so added to ~/.zshrc" ; }
elif [[ $current_shell == *bash ]]; then
  { echo $PATH | grep -q "${usr_local_bin_path}" ; } || { echo "export PATH=${usr_local_bin_path}"':$PATH' >> ~/.bashrc ; echo "${usr_local_bin_path} is not found in PATH so added to ~/.bashrc" ; }
else
  { echo $PATH | grep -q "${usr_local_bin_path}" ; } || { echo -e "$usr_local_bin_path is not found in \$PATH\nAdd the following line to PATH.\nexport PATH=$usr_local_bin_path:\$PATH\n" ; }
fi

echo -e "\nmaven2sbt is ready to use. Try"
echo -e "\n  ${app_executable_name} --help\n"
echo ""
echo -e "\nIf it does not work, make sure ${usr_local_bin_path} is in PATH"
echo "e.g.)"
echo -e "export PATH=$usr_local_bin_path:\$PATH\n"

