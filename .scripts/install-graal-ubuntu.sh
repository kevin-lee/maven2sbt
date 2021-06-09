#! /bin/bash

set -eu

app_executable_name=maven2sbt
app_name=maven2sbt-cli
app_version=${1:-1.4.0}
app_package_file="${app_name}-ubuntu-latest"
download_url="https://github.com/Kevin-Lee/maven2sbt/releases/download/v${app_version}/${app_package_file}"

usr_local_path=${HOME}
opt_location="${usr_local_path}/opt"
app_location="${opt_location}/${app_name}"
installed_app_bin_path="${app_location}/${app_executable_name}"
usr_local_bin_path="${usr_local_path}/bin"
app_bin_path="${usr_local_bin_path}/${app_executable_name}"

echo "app_executable_name=${app_executable_name}"
echo "app_name=${app_name}"
echo "app_version=${app_version}"
echo "app_package_file=${app_package_file}"
echo "download_url=${download_url}"

echo "usr_local_path=${usr_local_path}"
echo "opt_location=${opt_location}"
echo "app_location=${app_location}"
echo "installed_app_bin_path=${installed_app_bin_path}"
echo "usr_local_bin_path=${usr_local_bin_path}"
echo "app_bin_path=${app_bin_path}"

cd /tmp

curl -Lo $app_package_file $download_url

ls -l $app_package_file || { echo "maven2sbt version ${app_version} doesn't seem to exist." && false ; }
chmod ug+x $app_package_file

rm -R $app_location || true
mkdir -p $app_location
mv $app_package_file $installed_app_bin_path

echo ""
{ rm $app_bin_path && { echo "The existing $app_bin_path was found so it was removed." ; } } || { echo "No existing $app_bin_path was found. It's OK. Please ignore the 'No such file or directory' message." ; }
echo ""
echo "ln -s $installed_app_bin_path $app_bin_path"
ln -s $installed_app_bin_path $app_bin_path || true

echo -e "\nmaven2sbt is ready to use. Try"
echo -e "\n  ${app_executable_name} --help\n"
echo -e "\nIf it does not work, add ${usr_local_bin_path} to PATH"
echo "e.g.)"
echo -e "PATH=$usr_local_bin_path:\$PATH\n"
