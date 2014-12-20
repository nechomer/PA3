import os
import sys
from glob import glob
from subprocess import *

ProgFileList = glob("pa-3-input/*.ic")
for Progfile in ProgFileList:
	fname = str(Progfile).split('\\')[-1:][0]
	f1 = open('/pa-3-output/'+fname+'.out', 'w+')
	args = ['CompilationPA3_fat.jar', Progfile, -dump-symtab]
	process = Popen(['java', '-jar']+list(args), stdout=f1, stderr=f1)
	f1.close
	
ProgFileList = glob("pa-3-input/including_Library/*.ic")
for Progfile in ProgFileList:
	fname = str(Progfile).split('\\')[-1:][0]
	f1 = open('pa-3-output/'+fname+'.out', 'w+')
	args = ['CompilationPA3a_fat.jar', Progfile,"-LC:/Users/Roey/Desktop/PA3 test/pa-3-input/lib/libic.sig", -dump-symtab]
	process = Popen(['java', '-jar']+list(args), stdout=f1, stderr=f1)
	f1.close


