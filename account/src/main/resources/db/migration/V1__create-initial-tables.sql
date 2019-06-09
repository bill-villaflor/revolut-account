CREATE TABLE accounts(
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    currency VARCHAR(3) NOT NULL,
    creation_date TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE books(
	id UUID PRIMARY KEY,
	debit DECIMAL(19, 4),
	credit DECIMAL(19, 4),
	account_id UUID NOT NULL,
    creation_date TIMESTAMP WITH TIME ZONE NOT NULL,
    FOREIGN KEY(account_id) REFERENCES accounts(id)
);