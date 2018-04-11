#!/bin/bash

vagrant up
vagrant package
vagrant box add --name centos-base package.box
rm package.box
vagrant destroy -f