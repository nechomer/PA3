JAVA_PATH="/home/stas/programs/jdk1.7.0_45/bin"

$JAVA_PATH/javac -d ./bin -classpath 'lib/gearley.jar' `find ./ -name "*.java"`
mkdir pa-3-myoutput
for f in $(ls -l pa-3-input/ | grep ^- | awk '{print $9}' | grep "\.ic" | sed 's/.\{3\}$//')
do
	echo "********      TESTING $f      ********"
	$JAVA_PATH/java -classpath 'bin:lib/gearley.jar:src' Main pa-3-input/$f.ic > pa-3-myoutput/$f.out
	echo "diff -b pa-3-myoutput/$f.out pa-3-output/$f.sym "
	diff -b pa-3-myoutput/$f.out pa-3-output/$f.sym
done 
