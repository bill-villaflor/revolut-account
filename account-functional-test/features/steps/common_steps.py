import re
from string import Template

from assertpy import assert_that
from behave import then, given, when
from requests import api


@given('user prepares another API request')
def step_impl(context):
    prepare_request_step(context)


@given('user prepares an API request')
def prepare_request_step(context):
    context.request = {'body': {}}


@given('request body has {field} {value}')
def step_impl(context, field, value):
    if value.startswith('$'):
        alias = value[1:]
        context.request['body'][field] = context.response_stash[alias]
    elif value != 'NULL':
        context.request['body'][field] = value


@given('user submits a {http_method} request to {resource_path}')
def step_impl(context, http_method, resource_path):
    submit_request_step(context, http_method, resource_path)


@given('user keeps the {field} of the created resource as {alias}')
def step_impl(context, field, alias):
    assert_that(context.response.status_code).is_equal_to(201)
    response_body = context.response.json()

    if 'response_stash' not in context:
        context.response_stash = {}

    context.response_stash[alias] = response_body[field]


@when('user submits a {http_method} request to {resource_path}')
def submit_request_step(context, http_method, resource_path):
    endpoint = f"{context.config['base_url']}{resource_path}"
    pattern = re.compile(".*(\$[a-zA-Z_]+).*")
    path_param = pattern.search(resource_path)

    if path_param:
        key = path_param.group(1)[1:]
        template = Template(endpoint)
        endpoint = template.substitute({key: context.response_stash[key]})

    context.response = api.request(http_method, endpoint, json=context.request['body'])

@then('response status is {http_status}')
def step_impl(context, http_status):
    assert_that(str(context.response.status_code)).is_equal_to(http_status)


@then('response body contains {field}')
def step_impl(context, field):
    response_body = context.response.json()
    assert_that(response_body).contains(field)


@then('response body has {field} {value}')
def step_impl(context, field, value):
    response_body = context.response.json()
    assert_that(response_body).contains(field)
    assert_that(str(response_body[field])).is_equal_to(value)


@then('when user prepares another API request')
def step_impl(context):
    prepare_request_step(context)


@then('user submits a {http_method} request to {resource_path}')
def step_impl(context, http_method, resource_path):
    submit_request_step(context, http_method, resource_path)
