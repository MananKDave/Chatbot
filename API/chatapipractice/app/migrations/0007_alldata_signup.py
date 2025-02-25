# Generated by Django 3.2.18 on 2023-04-06 02:43

from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
        ('app', '0006_delete_signup'),
    ]

    operations = [
        migrations.CreateModel(
            name='alldata',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=255)),
                ('message', models.CharField(max_length=100000)),
                ('reply', models.CharField(max_length=100000)),
            ],
        ),
        migrations.CreateModel(
            name='signup',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=255)),
                ('email', models.CharField(max_length=255)),
                ('password', models.CharField(max_length=255)),
                ('cpassword', models.CharField(max_length=255)),
            ],
        ),
    ]
