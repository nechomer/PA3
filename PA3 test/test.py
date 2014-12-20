import os
import sys
from glob import glob
from subprocess import *

ProgFileList = glob("input/*.ic")
for Progfile in ProgFileList:
	fname = str(Progfile).split('\\')[-1:][0]
	f1 = open('output/'+fname+'.out', 'w+')
	args = ['PA3.jar', Progfile, "-dump-symtab"]
	process = Popen(['java', '-jar']+list(args), stdout=f1, stderr=f1)
	f1.close
	
ProgFileList = glob("input/including_Library/*.ic")
for Progfile in ProgFileList:
	fname = str(Progfile).split('\\')[-1:][0]
	f1 = open('output/'+fname+'.out', 'w+')
	args = ['PA3.jar', Progfile,"-Linput/including_Library/libic.sig", "-dump-symtab"]
	process = Popen(['java', '-jar']+list(args), stdout=f1, stderr=f1)
	f1.close


