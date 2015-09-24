#!/usr/bin/bash

BASE=129.22

OUTFILE=$1

if [[ $1 == "" ]]; then
    echo "Provide the outfile as the first argument"
    exit 1
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


for i in {0..255}; do
    for j in {0..255}; do
        printip ${BASE}.${i}.${j} >> $OUTFILE &
    done
    wait
done
