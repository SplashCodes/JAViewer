# -*- coding: utf-8 -*-
import os
import re
import pymysql
from bs4 import BeautifulSoup
import requests

# 请求头设置
header = {
    'Accept': '*/*',
    'Accept-Encoding': 'gzip',
    'Accept-Language': 'zh-CN,zh',
    'Cache-Control': 'no-cache',
    'Connection': 'keep-alive',
    'Cookie':'UM_distinctid=16788fb4536709-086e8195eed11f-b78173e-13c680-16788fb4537309; CNZZDATA1260841758=573693896-1544188651-%7C1544188651',
    'Host': 'www.zi2345.com',
    'Pragma': 'no-cache',
    'Upgrade-Insecure-Requests': '1',
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.80 Safari/537.36'
}

def web(url):
    session = requests.Session()
    session.trust_env = False
    db_data = session.get(url, headers=header)
    soup = BeautifulSoup(db_data.text, 'lxml')

    data = soup.select('body > div:nth-of-type(3) > div.mainArea > ul > li')
    for i in data :
        url = re.findall(re.compile(r"href=\"(.*)\" target"), str(i))[0]
        sql_str = "\'" + str(url) + "\'"
        to_mysql(sql_str)


def downimg(url):
    session = requests.Session()
    session.trust_env = False
    db_data = session.get(str('http://www.zi2345.com'+url), headers=header)
    soup = BeautifulSoup(db_data.text, 'lxml')
    filer = str(url).split('/')[-1].replace(".html","")
    imgurl = soup.select('#view1 > img')
    if not os.path.exists(filer):
        os.makedirs(filer)
    for i in imgurl :
        iurl = re.findall(re.compile(r"src=\"(.*)\""), str(i))[0]
        print(iurl)
        try:
            imgname = filer +"\\"+str(iurl).split('/')[-1]
            print(imgname)
            r = session.get(iurl)
            r.raise_for_status()
            # 使用with语句可以不用自己手动关闭已经打开的文件流
            with open(imgname, "wb") as f:  # 开始写文件，wb代表写二进制文件
                f.write(r.content)
            print("get img")
        except Exception as e:
            print(iurl + " error")



def find_mysql():
    db = pymysql.connect(host="127.0.0.1", user="root", password="root", db="imgc", port=3306)
    cur = db.cursor()
    sql = "select * from prict"
    try:
        cur.execute(sql)
        results = cur.fetchall()
        print(len(results))
        for row in results:
            print("begin find :"+row[0])
            downimg(row[0])
    except Exception as e:
        raise e
    finally:
        db.close()


def to_mysql(data):
    '''建表语句
     create table `prict`(
     `url` varchar(200) not null primary key,
     `id` integer
     )ENGINE=InnoDB DEFAULT CHARSET=utf8;
    '''
    db = pymysql.connect(host='127.0.0.1', user='root', password='root', port=3306, db='imgc', charset='gbk')
    cursor = db.cursor()
    sql = 'INSERT INTO prict(`url`) VALUES (' + data + ')'
    print(sql)
    try:
        cursor.execute(sql)
        print("Successful")
        db.commit()
    except:
        print('Failed')
        db.rollback()
    db.close()


if __name__ == '__main__':
    for i in range(1,44):
        url_base = 'http://www.zi2345.com/html/part/index22_'+ str(i) + '.html'
        web(url_base)
    find_mysql()