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
	source_account UUID,
	target_account UUID NOT NULL,
    creation_date TIMESTAMP WITH TIME ZONE NOT NULL,
    FOREIGN KEY(source_account) REFERENCES accounts(id),
    FOREIGN KEY(target_account) REFERENCES accounts(id)
);