#!/usr/bin/bash

BASE=129.22

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

    if [[ $out == "" ]]; then
        echo -n $out
    else
        echo $out | sed 's/^://'
    fi
}

loopthroughall() {
    for i in {0..255}; do
        for j in {0..255}; do
            TOTEST=${BASE}.${i}.${j}
            if [[ $1 == "" ]]; then
                processip2 $TOTEST &
            fi
            if [[ $1 == "full" ]]; then
                printip $TOTEST
            fi
        done
        wait
    done
}

loopthroughall $1
