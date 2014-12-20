import os
import sys
from glob import glob
from subprocess import *

ProgFileList = glob("input/*.ic")
for Progfile in ProgFileList:
	fname = str(Progfile).split('\\')[-1:][0]
	f1 = open('/output/'+fname+'.out', 'w+')
	args = ['CompilationPA3_fat.jar', Progfile, -dump-symtab]
	process = Popen(['java', '-jar']+list(args), stdout=f1, stderr=f1)
	f1.close
	
ProgFileList = glob("input/including_Library/*.ic")
for Progfile in ProgFileList:
	fname = str(Progfile).split('\\')[-1:][0]
	f1 = open('output/'+fname+'.out', 'w+')
	args = ['CompilationPA3a_fat.jar', Progfile,"-Lincluding_Library/libic.sig", -dump-symtab]
	process = Popen(['java', '-jar']+list(args), stdout=f1, stderr=f1)
	f1.close


