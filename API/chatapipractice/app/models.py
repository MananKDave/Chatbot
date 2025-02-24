from django.db import models


class signup(models.Model):
    name = models.CharField(max_length=255, null=False, blank=False)
    email = models.CharField(max_length=255, null=False, blank=False)
    password = models.CharField(max_length=255, null=False, blank=False)
    cpassword = models.CharField(max_length=255, null=False, blank=False)


class alldata(models.Model):
    name = models.CharField(max_length=255, null=False, blank=False)
    message = models.CharField(max_length=100000, null=False, blank=False)
    reply = models.CharField(max_length=100000, null=False, blank=False)
    links = models.CharField(max_length=100000, null=False, blank=False)
    date = models.CharField(max_length=255, null=False, blank=False)
    time = models.CharField(max_length=255, null=False, blank=False)
