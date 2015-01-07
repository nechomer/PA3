import os
import sys
from glob import glob
from subprocess import *

ProgFileList = glob("input/*.ic")
for Progfile in ProgFileList:
	fname = str(Progfile).split('\\')[-1:][0]
	f1 = open('output/'+fname+'.out', 'w+')
	args = ['PA4.jar', Progfile, "-print-ast"]
	process = Popen(['java', '-jar']+list(args), stdout=f1, stderr=f1)
	f1.close
	
ProgFileList = glob("input/including_Library/*.ic")
for Progfile in ProgFileList:
	fname = str(Progfile).split('\\')[-1:][0]
	f1 = open('output/'+fname+'.out', 'w+')
	args = ['PA4.jar', Progfile,"-Linput/including_Library/libic.sig", "-print-ast"]
	process = Popen(['java', '-jar']+list(args), stdout=f1, stderr=f1)
	f1.close


