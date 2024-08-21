#!/bin/zsh

protoc -I/usr/local/include -I. -I$GOPATH/src \
       --include_imports --include_source_info --descriptor_set_out=./user_service.pb ./user_service.proto


