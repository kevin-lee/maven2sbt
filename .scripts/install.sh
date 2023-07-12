#! /bin/bash

set -eu

app_executable_name=maven2sbt
app_name=maven2sbt-cli
app_version=${1:-1.5.0}
versioned_app_name="${app_name}-${app_version}"
app_zip_file="${versioned_app_name}.zip"
download_url="https://github.com/kevin-lee/maven2sbt/releases/download/v${app_version}/${app_zip_file}"

usr_local_path="/usr/local"
opt_location="${usr_local_path}/opt"
app_location="${opt_location}/${app_name}"
installed_app_bin_path="${app_location}/bin/${app_executable_name}"
usr_local_bin_path="${usr_local_path}/bin"
app_bin_path="${usr_local_bin_path}/${app_executable_name}"

echo "app_executable_name=${app_executable_name}"
echo "app_name=${app_name}"
echo "app_version=${app_version}"
echo "versioned_app_name=${versioned_app_name}"
echo "app_zip_file=${app_zip_file}"
echo "download_url=${download_url}"

echo "usr_local_path=${usr_local_path}"
echo "opt_location=${opt_location}"
echo "app_location=${app_location}"
echo "installed_app_bin_path=${installed_app_bin_path}"
echo "usr_local_bin_path=${usr_local_bin_path}"
echo "app_bin_path=${app_bin_path}"

cd /tmp

curl -Lo $app_zip_file $download_url

unzip $app_zip_file || { echo "maven2sbt version ${app_version} doesn't seem to exist." && rm $app_zip_file && false ; }

mkdir -p $opt_location
rm -R $app_location || true
mv $versioned_app_name $app_location

mkdir -p $usr_local_bin_path
echo ""
{ rm $app_bin_path && { echo "The existing $app_bin_path was found so it was removed." ; } } || { echo "No existing $app_bin_path was found. It's OK. Please ignore the 'No such file or directory' message." ; }
echo ""

echo "ln -s $installed_app_bin_path $app_bin_path"
ln -s $installed_app_bin_path $app_bin_path || true

rm $app_zip_file

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
