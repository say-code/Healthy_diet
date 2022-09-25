from pyecharts import options as opts
from pyecharts.charts import Bar, Grid, Line, Liquid, Page, Pie
from pyecharts.commons.utils import JsCode
from pyecharts.components import Table
from pyecharts.faker import Faker
import pymysql
import time
from pyecharts import options as opts

optX = opts.InitOpts(width="45%", height="400px")

while(True):
    # 打开数据库连接
    db = pymysql.connect(host="106.14.141.171", user="root", password="TabStudio@3306", database="healthy_diet",
                         charset='utf8')

    # 使用cursor()方法获取操作游标
    cursor = db.cursor()
    # SQL 查询语句
    sql = "select year(checkout_time),month(checkout_time), day(checkout_time), sum(amount) from orders group by year(checkout_time) ,month(checkout_time) ,day(checkout_time);"
    cursor.execute(sql)
    # print("data"+str(cursor.fetchall()[0][3]))
    datas = []
    # cursor.fetchall()
    for data in cursor.fetchall():
        datas.append(data[3])

    start = len(datas)-30
    if start < 0:
        start = 0
    def bar_datazoom_slider() -> Bar:
        c = (
            Line(init_opts=optX)
            .add_xaxis(Faker.days_attrs)
            .add_yaxis("销售额",datas[start:])
            .set_global_opts(
                title_opts=opts.TitleOpts(title="每日销售额数据总览"),
                datazoom_opts=[opts.DataZoomOpts()],
            )
        )
        return c


    cursor3 = db.cursor()
    cursor4 = db.cursor()
    names = "select name,id from dish"
    cursor3.execute(names)
    # print(cursor3.fetchall())
    sql1 = "select id from dish"
    dishes = {}
    for dish in cursor3.fetchall():
        dishes[dish[1]] = dish[0]
    # dish = [cursor3.fetchall()]
    cursor4.execute(sql1)
    # print(cursor4.fetchall())
    foods = cursor4.fetchall()
    print(foods)
    dict = {}

    cursor2 = db.cursor()
    for ids in foods:
        sql2 = "select sum(number) from order_detail where dish_id ="+str(ids[0])+";"
        cursor2.execute(sql2)
        number = cursor2.fetchall()[0][0]
        if number:
            dict[dishes[ids[0]]] = int(number)

    print(dict)
    def line_markpoint() -> Line:
        c = (
            Bar(init_opts=optX)
            .add_xaxis(list(dict.keys()))
            .add_yaxis(
                "各商品销售数量",
                list(dict.values()),
                markpoint_opts=opts.MarkPointOpts(data=[opts.MarkPointItem(type_="min")]),
            )
            .set_global_opts(title_opts=opts.TitleOpts(title="各商品销售量"),
                             datazoom_opts=[opts.DataZoomOpts()]
                             )
        )
        return c
    cursor2.close()

    def page_simple_layout():
        page = Page(layout=Page.SimplePageLayout)
        page.add(
            bar_datazoom_slider(),
            line_markpoint(),

        )
        page.render("F:/桌面/Heathy_diet/target/classes/backend/page/home/chart.html")

    page_simple_layout()
    db.close()
    cursor.close()
    time.sleep(60*60*24)


