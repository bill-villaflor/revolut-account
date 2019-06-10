CREATE TABLE accounts(
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    currency VARCHAR(3) NOT NULL,
    creation_date TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE books(
	id UUID PRIMARY KEY,
	amount DECIMAL(19, 4),
	source UUID,
	destination UUID,
    creation_date TIMESTAMP WITH TIME ZONE NOT NULL,
    FOREIGN KEY(source) REFERENCES accounts(id),
    FOREIGN KEY(destination) REFERENCES accounts(id)
);