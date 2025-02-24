import json

from django.shortcuts import render
from rest_framework.views import APIView
from django.http.response import JsonResponse
from app.chatapi import chatapi
from app.serializers import signupserializers, alldataserializers
from app.models import signup, alldata


class TestView(APIView):
    def post(self, request):

        res0 = []
        res1 = []
        res2 = []
        res3 = []

        if request.method == 'POST':
            name = request.POST['name']
            message = request.POST['message']
            date = request.POST['date']
            time = request.POST['time']

            reply = chatapi(message)
            links = TestViewlinks(message)

            newdata = alldata()
            newdata.name = name
            newdata.message = message
            newdata.reply = reply
            newdata.links = links
            newdata.date = date
            newdata.time = time
            newdata.save()
            """allolddata0 = alldata.objects.all()
            allolddata1 = alldataserializers(allolddata0, many=True)
            allolddata2 = json.dumps(allolddata1.data)
            olddata3 = json.loads(allolddata2)

            for item in olddata3:
                l0 = item["name"]
                res0.append(l0)

            for item in olddata3:
                l1 = item["message"]
                res1.append(l1)

            for item in olddata3:
                l2 = item["date"]
                res2.append(l2)

            for item in olddata3:
                l3 = item["time"]
                res3.append(l3)

            if len(olddata3) == 0:
                print("NEW")
                newdata = alldata()
                newdata.name = name
                newdata.message = message
                newdata.reply = reply
                newdata.date = date
                newdata.time = time
                newdata.save()
            else:
                for i in range(len(res0)):
                    if name == res0[i]:
                        if message == res1[i]:
                            if date == res2[i]:
                                if time == res3[i]:
                                    print("UPDATED")
                                    newdata = alldata()
                                    newdata.name[i] = name
                                    newdata.message[i] = message
                                    newdata.reply[i] = reply
                                    newdata.date[i] = date
                                    newdata.time[i] = time
                                    newdata.save()
                                else:
                                    print("NEWWW")
                                    newdata = alldata()
                                    newdata.name = name
                                    newdata.message = message
                                    newdata.reply = reply
                                    newdata.date = date
                                    newdata.time = time
                                    newdata.save()"""

            return JsonResponse("Done", safe=False)


def TestViewlinks(message):
    links = "available links or URLs or HTTP or HTTPS of " + message
    reply = chatapi(links)

    return reply


class TestViewdata(APIView):
    def get(self, request):
        res0 = []
        res00 = []
        res01 = []
        res1 = []
        res2 = []
        res3 = []
        dic1 = {}
        dic0 = {}
        l = []

        allolddata0 = alldata.objects.all()
        allolddata1 = alldataserializers(allolddata0, many=True)
        allolddata2 = json.dumps(allolddata1.data)
        olddata3 = json.loads(allolddata2)

        for item in olddata3:
            l0 = item["name"]
            res0.append(l0)

        for i in res0:
            if i not in res00:
                res00.append(i)

        for item in olddata3:
            l1 = item["message"]
            res1.append(l1)

        for item in olddata3:
            l2 = item["reply"]
            res2.append(l2)

        for item in olddata3:
            l3 = item["links"]
            res3.append(l3)

        """hour_minute_list = []

        for timestamp in res3:
            hour, minute, second = timestamp.split(":")[:3]
            hour_minute = f"{hour}:{minute}"
            hour_minute_list.append(hour_minute)

        for j in range(len(res0)):
            dic1[hour_minute_list[j]] = []

        for jj in range(len(res0)):
            dic1[hour_minute_list[jj]].append(res2[jj])

        a1 = list(dic1.values())
        al = len(a1)
        a = a1[al - 1]
        final_reply = ' '.join(a)"""

        dic1["chatgptreply"] = []

        length = len(res2)
        """dic1 = {"reply": res2[length - 1], "links": res3[length - 1]}"""
        dic0["message"] = res1[length - 1]
        dic0["reply"] = res2[length - 1]
        dic0["links"] = res3[length - 1]
        dic1["chatgptreply"].append(dic0)

        return JsonResponse(dic1, safe=False)


class TestViewRegister(APIView):
    def post(self, request):
        res0 = []
        if request.method == 'POST':
            email = request.POST['email']

            newdata = signupserializers(data=request.data)
            olddata0 = signup.objects.all()
            olddata1 = signupserializers(olddata0, many=True)
            olddata2 = json.dumps(olddata1.data)
            olddata3 = json.loads(olddata2)

            for item in olddata3:
                l0 = item["email"]
                res0.append(l0)

            if email in res0:
                return JsonResponse("Account Exists", safe=False)
            else:
                if newdata.is_valid():
                    newdata.save()
                    return JsonResponse("Account Has Been Created", safe=False)


class TestViewLogin(APIView):
    def post(self, request):
        res0 = []
        res1 = []
        res2 = []
        if request.method == 'POST':
            email = request.POST['email']
            password = request.POST['password']

            olddata0 = signup.objects.all()
            olddata1 = signupserializers(olddata0, many=True)
            olddata2 = json.dumps(olddata1.data)
            olddata3 = json.loads(olddata2)

            for item in olddata3:
                l0 = item["name"]
                res0.append(l0)

            for item in olddata3:
                l1 = item["email"]
                res1.append(l1)

            for item in olddata3:
                l2 = item["password"]
                res2.append(l2)

            for i in range(len(res0)):
                if email in res1:
                    if password in res2:
                        if res1[i] == email and res2[i] == password:
                            return JsonResponse("Welcome " + res0[i], safe=False)
                    else:
                        return JsonResponse("Password Doesn't Match", safe=False)
                else:
                    return JsonResponse("No Account", safe=False)
            return JsonResponse("No Account", safe=False)


class TestViewHistory(APIView):
    def get(self, request):

        res0 = []
        res00 = []
        res1 = []
        res01 = []
        res2 = []
        res02 = []
        res3 = []
        res03 = []
        res4 = []
        res04 = []
        res5 = []
        dic0 = {}
        dic1 = {}
        dic2 = {}

        allolddata0 = alldata.objects.all()
        allolddata1 = alldataserializers(allolddata0, many=True)
        allolddata2 = json.dumps(allolddata1.data)
        olddata3 = json.loads(allolddata2)

        for item in olddata3:
            l0 = item["name"]
            res0.append(l0)

        for i in res0:
            if i not in res00:
                res00.append(i)

        for item in olddata3:
            l1 = item["message"]
            res1.append(l1)

        for i in res1:
            if i not in res01:
                res01.append(i)

        for item in olddata3:
            l2 = item["reply"]
            res2.append(l2)

        for i in res2:
            if i not in res02:
                res02.append(i)

        for item in olddata3:
            l3 = item["time"]
            res3.append(l3)

        """hour_minute_list = []

        for timestamp in res3:
            hour, minute, second = timestamp.split(":")[:3]
            hour_minute = f"{hour}:{minute}"
            hour_minute_list.append(hour_minute)"""

        for i in res3:
            if i not in res03:
                res03.append(i)

        for item in olddata3:
            l4 = item["date"]
            res4.append(l4)

        for item in olddata3:
            l5 = item["links"]
            res5.append(l5)

        l = []
        for i in range(len(res00)):
            for ii in range(len(res0)):
                if res00[i] == res0[ii]:
                    l.append(res4[ii])

        for i in l:
            if i not in res04:
                res04.append(i)

        for i in range(len(res00)):
            dic0[res00[i]] = []

        for j0 in range(len(res00)):
            for j1 in range(len(res03)):
                for j2 in range(len(res0)):
                    if res00[j0] == res0[j2]:
                        if res03[j1] == res3[j2]:
                            dic1 = {"Date": res4[j2], "Time": res03[j1], "Message": res1[j2], "Reply": res2[j2], "Links": res5[j2]}
                            dic0[res00[j0]].append(dic1.copy())

        return JsonResponse(dic0, safe=False)


class TestViewHistoryLocation(APIView):
    def get(self, request):

        res0 = []
        res00 = []
        res1 = []
        res01 = []
        res2 = []
        res02 = []
        res3 = []
        res03 = []
        res4 = []
        res04 = []
        res5 = []
        dic0 = {}
        dic2 = {}
        dic3 = {}
        dic4 = {}

        allolddata0 = alldata.objects.all()
        allolddata1 = alldataserializers(allolddata0, many=True)
        allolddata2 = json.dumps(allolddata1.data)
        olddata3 = json.loads(allolddata2)

        for item in olddata3:
            l0 = item["name"]
            res0.append(l0)

        for i in res0:
            if i not in res00:
                res00.append(i)

        for item in olddata3:
            l1 = item["message"]
            res1.append(l1)

        for i in res1:
            if i not in res01:
                res01.append(i)

        for item in olddata3:
            l2 = item["reply"]
            res2.append(l2)

        for i in res2:
            if i not in res02:
                res02.append(i)

        for item in olddata3:
            l3 = item["time"]
            res3.append(l3)

        """hour_minute_list = []

        for timestamp in res3:
            hour, minute, second = timestamp.split(":")[:3]
            hour_minute = f"{hour}:{minute}"
            hour_minute_list.append(hour_minute)"""

        for i in res3:
            if i not in res03:
                res03.append(i)

        for item in olddata3:
            l4 = item["date"]
            res4.append(l4)

        for item in olddata3:
            l5 = item["links"]
            res5.append(l5)

        l = []
        for i in range(len(res00)):
            for ii in range(len(res0)):
                if res00[i] == res0[ii]:
                    l.append(res4[ii])

        for i in l:
            if i not in res04:
                res04.append(i)

        for i in range(len(res00)):
            dic0[res00[i]] = []

        for j0 in range(len(res00)):
            for j1 in range(len(res03)):
                for j2 in range(len(res0)):
                    if res00[j0] == res0[j2]:
                        if res03[j1] == res3[j2]:
                            dic1 = {"Date": res4[j2], "Time": res03[j1], "Message": res1[j2], "Reply": res2[j2], "Links": res5[j2]}
                            dic0[res00[j0]].append(dic1.copy())

        data_dict = json.loads(json.dumps(dic0))
        username = list(dic0.keys())
        userdata = list(dic0.values())

        for i in range(len(username)):
            user = username[i]
            dic2[user] = []
            if user in data_dict:
                dic2[user].append(len(data_dict[user]))

        for key in dic0:
            dic3[key] = []
            for i, chat in enumerate(dic0[key]):
                dic3[key].append({str(i): [chat]})

        return JsonResponse(dic3, safe=False)
