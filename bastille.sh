#!/usr/bin/bash

BASE=129.22

if [[ $CPUS == "" ]]; then
    CPUS=8
fi

getoneip() {
    IP=$(dig -x $1 @8.8.8.8 | grep arpa | grep PTR | awk '{print $5}' | grep -i edu)

    if [[ $IP == "" ]]; then
        return 1
    fi
    echo ${IP}
}

printip() {
    ADDR=$(getoneip $1 || echo -n '')

    if [[ $ADDR == "" ]]; then
        echo -n $ADDR
    else
        echo ${1}: ${ADDR}
    fi
}

processip1() {
    printip $1 | grep tmp | awk '{print $2}' | sed 's/tmp//g' | sed 's/[.].*$//g'
}

processip2() {
    out=""
    for i in `processip1 $1 | fold -w2`; do
        out=${out}:${i}
    done

    out=`echo $out | grep -E '([0-9a-fA-F]{2}:){5}[0-9a-fA-F]'`

    if [[ $out == "" ]]; then
        echo -n $out
    else
        echo $out | sed 's/^://'
    fi
}

loopthroughall() {
    for i in {0..255}; do
        c=-1
        for j in {0..255}; do
            c=$((c+1))
            TOTEST=${BASE}.${i}.${j}
            if [[ $1 == "" ]]; then
                processip2 $TOTEST &
            fi
            if [[ $1 == "full" ]]; then
                printip $TOTEST &
            fi
            if [[ $c == $CPUS ]]; then
                wait
                c=-1
            fi
        done
        wait
    done
}

echo "Scanning ${BASE}.0.0 to ${BASE}.255.255 with max ${CPUS} threads" >&2

loopthroughall $1
