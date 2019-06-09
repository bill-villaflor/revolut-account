from assertpy import assert_that
from behave import then, given, when
from requests import api
from string import Template


@given('user prepares another API request')
def step_impl(context):
    prepare_request_step(context)


@given('user prepares an API request')
def prepare_request_step(context):
    context.request = {'body': {}}


@given('request body has {field} {value}')
def step_impl(context, field, value):
    if value != 'NULL':
        context.request['body'][field] = value


@given('user submits a {http_method} request to {resource_path}')
def step_impl(context, http_method, resource_path):
    submit_request_step(context, http_method, resource_path)


@given('request path references the generated resource id')
def step_impl(context):
    assert_that(context.response.status_code).is_equal_to(201)
    response_body = context.response.json()
    context.created_resource_id = response_body['id']


@when('user submits a {http_method} request to {resource_path}')
def submit_request_step(context, http_method, resource_path):
    endpoint = f"{context.config['base_url']}{resource_path}"

    if http_method == 'GET' and 'created_resource_id' in context:
        template = Template(endpoint)
        endpoint = template.substitute(id=context.created_resource_id)

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
