license=`cat LICENSE`
license_head=" * Copyright 2012 Anjuke Inc."

for f in `find . -name "*.java" -printf "%u %g %p\n" |awk '{print $3}'` 
do
    file_head="`head -n 2 $f|tail -1`"
    file_head=`echo "$file_head"`
    ilicense_head=`echo "$license_head"`	
    if [ "$file_head" != "$license_head" ];then
	    echo "add copyright on $f"
        file=`cat $f` 
        echo  "$license" > "$f"
        echo  "$file" >> "$f"
    fi
done
