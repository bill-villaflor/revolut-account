from configparser import ConfigParser


def before_all(context):
    config = ConfigParser()
    config.read('behave.ini')
    profile_env = config._sections['profile']['env']
    context.config = config._sections[profile_env]
